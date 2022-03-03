package pw.mihou.alisa.modules.http;

import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pw.mihou.alisa.modules.configuration.AlisaConfiguration;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class AlisaHttpCall {

    private static final OkHttpClient CLIENT = new OkHttpClient.Builder()
            .callTimeout(Duration.ofMinutes(5))
            .connectTimeout(Duration.ofMinutes(5))
            .readTimeout(Duration.ofMinutes(5))
            .build();

    private static final String USER_AGENT = "Amelia/2.0 (+http://www.github.com/Amelia-chan/Amelia/bot.txt)";
    private static final Logger LOGGER = LoggerFactory.getLogger("Alisa - Http Client");

    private final Request request;
    private final CompletableFuture<Response> future = new CompletableFuture<>();
    private final AtomicLong initialTime = new AtomicLong(-1);
    private final AtomicLong timeTaken = new AtomicLong(-1);
    private final List<IOException> exceptions = new ArrayList<>();
    private final AtomicInteger retries = new AtomicInteger(0);
    private final AtomicInteger maximumRetries = new AtomicInteger(10);
    private final AtomicBoolean lock = new AtomicBoolean(false);

    /**
     * Creates a new {@link AlisaHttpCall} that can handle retries and all those
     * other fancy stuff in your stead.
     *
     * @param builder   The builder to use for this request.
     */
    public AlisaHttpCall(Request.Builder builder) {
        if (AlisaConfiguration.SIGNATURE != null) {
            builder.addHeader("Amelia-Signature", AlisaConfiguration.SIGNATURE);
        }

        builder.addHeader("User-Agent", USER_AGENT);
        this.request = builder.build();
    }

    /**
     * Sets the maximum amount of times that {@link AlisaHttpCall} should
     * retry if ever it tries an exception.
     *
     * @param amount    The amount of errors maximum.
     * @return          The {@link AlisaHttpCall} for chain-calling methods.
     */
    public AlisaHttpCall maximumRetries(int amount) {
        this.maximumRetries.set(amount);
        return this;
    }

    /**
     * Gets the total elapsed time that this call has been running.
     *
     * @return  The elapsed time that this call has been running.
     */
    public Duration elapsed() {
        if (initialTime.get() == -1) {
            return Duration.ofNanos(0L);
        }

        if (timeTaken.get() == -1 ) {
            return Duration.ofNanos(System.nanoTime() - initialTime.get());
        }

        return Duration.ofNanos(timeTaken.get() - initialTime.get());
    }

    /**
     * Gets the list of {@link IOException} that was received during the
     * time of calling. This does not include the final {@link IOException} that
     * caused the retry to stop entirely.
     *
     * @return  The list of {@link IOException} for this call.
     */
    public List<IOException> exceptions(){
        return exceptions;
    }

    /**
     * Submits the request to the caller to handle the requests and
     * attempt to get a response up to the specified maximum retries otherwise
     * retries an {@link IOException} exceptionally.
     *
     * @return  The {@link CompletableFuture} received for this call.
     */
    public CompletableFuture<Response> submit() {
        if (!lock.get()) {
            execute();
        }

        return future;
    }

    /**
     * Executes the request with the specified settings and keeps retrying
     * for any issues up to the specified maximum retries if any, otherwise, passes the response
     * to the future which can be acquired from the {@link AlisaHttpCall#submit()} method.
     */
    private void execute() {
        lock.set(true);
        initialTime.set(System.nanoTime());
        CLIENT.newCall(this.request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException exception) {
                if (retries.incrementAndGet() > maximumRetries.get()) {
                    timeTaken.set(System.nanoTime());
                    future.completeExceptionally(exception);
                    return;
                }

                exceptions.add(exception);
                execute();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                timeTaken.set(System.nanoTime());
                future.complete(response);
            }
        });
    }

}
