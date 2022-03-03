package pw.mihou.akari;

import ch.qos.logback.classic.Logger;
import com.squareup.moshi.Moshi;
import io.javalin.Javalin;
import org.slf4j.LoggerFactory;
import pw.mihou.akari.configuration.AkariConfiguration;
import pw.mihou.dotenv.Dotenv;

public class Akari {

    private static final Logger logger = (Logger) LoggerFactory.getLogger("Akari");
    private static final Moshi moshi = new Moshi.Builder().build();

    /**
     * Executed during startup as the first entrypoint.
     *
     * @param ignored  The args from starting up the console, ignored.
     */
    public static void main(String[] ignored) {
        Dotenv.asReflective().reflectTo(AkariConfiguration.class);
        logger.info(
                "Akari-chan is now starting up. [port={}, secret={}]",
                AkariConfiguration.WEBSOCKET_PORT,
                AkariConfiguration.SECRET
        );
    }

    /**
     * Gets the logging engine for Akari-chan.
     *
     * @return  The logging engine that Akari-chan uses.
     */
    public static Logger getLogger() {
        return logger;
    }

    /**
     * Gets the {@link Moshi} instance for Akari-chan.
     *
     * @return  The {@link Moshi} instance.
     */
    public static Moshi getMoshi() {
        return moshi;
    }
}
