package pw.mihou.alisa.modules.exceptions.handler;

import io.sentry.Sentry;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

public class AlisaExceptionHandler {

    /**
     * Accepts the throwable and handles the exception.
     *
     * @param throwable The throwable to accept.
     */
    public static void accept(Throwable throwable) {
        LoggerFactory.getLogger("Alisa").error("An exception was thrown.", throwable);
        Sentry.captureException(throwable);
    }

    /**
     * Accepts the throwable and handles the exception, this is used for
     * {@link java.util.concurrent.CompletableFuture#exceptionally(Function)} which requires
     * a return type.
     *
     * @param throwable The throwable to accept.
     * @return null
     */
    public static <T> T exceptionally(Throwable throwable) {
        accept(throwable);
        return null;
    }

}
