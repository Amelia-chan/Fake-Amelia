package pw.mihou.akari.websocket.listeners;

import io.javalin.websocket.WsMessageContext;
import org.json.JSONObject;
import pw.mihou.akari.websocket.facade.AkariWebsocket;
import pw.mihou.akari.websocket.listeners.modules.AkariOnTestRequest;
import pw.mihou.alisa.modules.AlisaMessage;
import pw.mihou.alisa.modules.exceptions.AlisaException;
import pw.mihou.alisa.modules.threadpools.AlisaThreadPool;

import java.util.ArrayList;
import java.util.List;

public class AkariWebsocketListenerRepository {

    public static final List<AkariWebsocketListener> listeners = new ArrayList<>();

    static {
        listeners.add(new AkariOnTestRequest());
    }

    /**
     * Gets all the listener that matches the message that was received.
     *
     * @param connection   The connection that was received on the server.
     */
    public static void send(AkariWebsocket websocket, WsMessageContext connection) {
        try {
            JSONObject raw = new JSONObject(connection.message());
            AlisaMessage message = new AlisaMessage(raw.getString("data"), raw.getString("className"));

            listeners.stream()
                    .filter(listener -> listener.accepts().equals(message.className()))
                    .forEach(listener -> AlisaThreadPool.submitTask(() -> listener.onMessage(websocket, connection, connection.message())));
        } catch (Exception e) {
            connection.send(new AlisaMessage(new AlisaException(e.getMessage()), AlisaException.class.getName()));
        }
    }

}
