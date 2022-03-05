package pw.mihou.akari;

import ch.qos.logback.classic.Logger;
import com.squareup.moshi.Moshi;
import io.javalin.Javalin;
import org.slf4j.LoggerFactory;
import pw.mihou.akari.configuration.AkariConfiguration;
import pw.mihou.alisa.modules.database.AlisaDatabaseClient;
import pw.mihou.dotenv.Dotenv;

public class Akari {

    static {
        Dotenv.asReflective().reflectTo(AkariConfiguration.class);
    }

    private static final Logger logger = (Logger) LoggerFactory.getLogger("Akari");
    private static final Moshi moshi = new Moshi.Builder().build();
    private static final AlisaDatabaseClient databaseClient = new AlisaDatabaseClient(
            AkariConfiguration.MONGODB_URI
    );

    /**
     * Executed during startup as the first entrypoint.
     *
     * @param ignored  The args from starting up the console, ignored.
     */
    public static void main(String[] ignored) {
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

    /**
     * Gets the {@link AlisaDatabaseClient} for Akari-chan.
     *
     * @return  The {@link AlisaDatabaseClient} instance.
     */
    public static AlisaDatabaseClient getDatabaseClient() {
        return databaseClient;
    }
}
