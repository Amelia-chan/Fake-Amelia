package pw.mihou.akari.websocket.core;

import io.javalin.Javalin;
import io.javalin.core.util.JavalinException;
import io.javalin.websocket.WsContext;
import pw.mihou.akari.Akari;
import pw.mihou.akari.configuration.AkariConfiguration;
import pw.mihou.akari.websocket.defaults.AkariDefaultMessages;
import pw.mihou.akari.websocket.facade.AkariWebsocket;
import pw.mihou.akari.websocket.listeners.AkariWebsocketListener;
import pw.mihou.akari.websocket.listeners.AkariWebsocketListenerRepository;
import pw.mihou.alisa.modules.AlisaMessage;
import pw.mihou.alisa.modules.exceptions.AlisaException;
import pw.mihou.alisa.modules.exceptions.handler.AlisaExceptionHandler;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class AkariWebsocketCore implements AkariWebsocket {

    private final BlockingQueue<AlisaMessage> messages = new LinkedBlockingQueue<>();
    private final Map<String, WsContext> sessions = new ConcurrentHashMap<>();

    private final Javalin javalin;

    private final AtomicBoolean open = new AtomicBoolean(false);
    private final AtomicBoolean closing = new AtomicBoolean(false);

    public AkariWebsocketCore() {
        this.javalin = Javalin.create(config -> config.showJavalinBanner = false)
                .ws("/", config -> {
                    config.onConnect(connection -> {
                        if (closing.get()) {
                            connection.closeSession(1013, AkariDefaultMessages.CLOSING_TIME);
                            return;
                        }

                        String authorization = connection.header("Authorization");

                        if (Objects.isNull(authorization)) {
                            connection.closeSession(1008, AkariDefaultMessages.INVALID_AUTHORIZATION);
                            return;
                        }

                        if (!authorization.equals("BEARER " + AkariConfiguration.SECRET)) {
                            connection.closeSession(1008, AkariDefaultMessages.INVALID_AUTHORIZATION);
                            return;
                        }

                        sessions.put(connection.getSessionId(), connection);
                        Akari.getLogger().info(
                                "A connection was established. [session={}, address={}]",
                                connection.getSessionId(),
                                connection.session.getRemoteAddress().toString()
                        );

                        // This is to ensure that for every session added, the queue is
                        // started.
                        startQueue();
                    });

                    config.onClose(connection -> {
                        sessions.remove(connection.getSessionId());
                        Akari.getLogger().info(
                                "A connection was closed. [session={}, address={}]",
                                connection.getSessionId(),
                                connection.session.getRemoteAddress().toString()
                        );
                    });

                    config.onMessage(connection -> {
                        if (closing.get()) {
                            connection.send(AkariDefaultMessages.UNACCEPTED_CLOSING);
                            return;
                        }

                        if (connection.message().equalsIgnoreCase("PING")) {
                            connection.send("PONG");
                            return;
                        }

                        AkariWebsocketListenerRepository.send(this, connection);
                    });

                    config.onError(connection -> {
                        Throwable error = connection.error();
                        if (error != null && error.getMessage() != null) {
                            AlisaExceptionHandler.accept(error);

                            if (error.getMessage() == null) {
                                this.send(connection.getSessionId(), new AlisaException(AkariDefaultMessages.UNKNOWN_EXCEPTION));
                                return;
                            }

                            this.send(connection.getSessionId(), new AlisaException(error.getMessage()));
                        } else {
                            this.send(connection.getSessionId(), new AlisaException(AkariDefaultMessages.UNKNOWN_EXCEPTION));
                        }
                    });
                });

        Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
    }

    @Override
    public void send(Object object) {
        messages.add(new AlisaMessage(object, object.getClass().getName()));
    }

    @Override
    public void send(String session, Object object) {
        if (!sessions.containsKey(session)) {
            AlisaExceptionHandler.accept(
                    new IllegalArgumentException("A request to send data to " + session + " failed because: Session was not found.")
            );
            return;
        }

        sessions.get(session).send(new AlisaMessage(object, object.getClass().getName()));
    }

    @Override
    public void start() {
        try {
            javalin.start("0.0.0.0", AkariConfiguration.WEBSOCKET_PORT);
        } catch (JavalinException exception) {
            AlisaExceptionHandler.accept(exception);
        }
    }

    @Override
    public void stop() {
        try {
            if (closing.get()) {
                return;
            }

            closing.set(true);
            Akari.getLogger().info("The websocket is now performing graceful message sending...");
            if (open.get()) {
                return;
            }

            if (!messages.isEmpty()) {
                startQueue();
            }
        } catch (JavalinException exception) {
            AlisaExceptionHandler.accept(exception);
        }
    }

    /**
     * Pushes the stop execution after confirming that all messages were sent
     * to their respective clients without an issue.
     */
    public void finishedQueueOnClosing() {
        try {
            Akari.getLogger().info("Closing websocket connection....");
            javalin.stop();
        } catch (JavalinException exception) {
            AlisaExceptionHandler.accept(exception);
        }
    }

    /**
     * Pushes the queue active to start broadcasting messages onto the global
     * broadcast channels.
     */
    private void startQueue() {
        CompletableFuture.runAsync(() -> {
            if (open.get()) {
                return;
            }

            if (sessions.isEmpty()) {
                Akari.getLogger().error("There are no sessions active on the socket, pushing all messages back until a session opens.");
                return;
            }

            open.set(true);
            while (!messages.isEmpty()) {
                AlisaMessage message = messages.poll();

                if (message != null) {
                    sessions.values().forEach(connection -> {
                        try {
                            String messageable = message.toMessageable(Akari.getMoshi());

                            connection.send(messageable).get();
                            Akari.getLogger().debug("A message was sent to a client. [session={}, message={}]",
                                    connection.getSessionId(),
                                    messageable
                            );
                        } catch (InterruptedException | ExecutionException e) {
                            // Don't queue the message again since another client might have received it already.
                            AlisaExceptionHandler.accept(e);
                        }
                    });
                } else {
                    AlisaExceptionHandler.accept(
                            new NoSuchElementException("A message polled has returned null, what does that mean?")
                    );
                }
            }
            open.set(false);

            if (closing.get()) {
                finishedQueueOnClosing();
            }
        });
    }
}
