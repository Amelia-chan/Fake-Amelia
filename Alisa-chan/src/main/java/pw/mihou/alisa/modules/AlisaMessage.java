package pw.mihou.alisa.modules;

import com.squareup.moshi.Moshi;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Optional;

public record AlisaMessage(Object data, String className) {

    /**
     * Gets the {@link String} equivalent message of this message, this will only work
     * if this is from a request message.
     *
     * @return  The message to receive.
     */
    @Nullable
    public String getMessage() {
        if (data instanceof String) {
            return (String) data;
        }

        return null;
    }

    /**
     * Resolves this message into the specific type mentioned, this will only work
     * if this is from a request message.
     *
     * @param moshi         The moshi instance to use.
     * @param type          The type to resolve into.
     * @param <T>           The type to resolve into.
     * @return              The resolved data object.
     * @throws IOException  If the data doesn't match the structure of the
     * resolved data object.
     */
    @Nullable
    public <T> T resolve(Moshi moshi, Class<T> type) throws IOException {
        if (data instanceof String) {
            return moshi.adapter(type).fromJson((String) data);
        }

        return null;
    }

    /**
     * Transforms this {@link AlisaMessage} instance into a messageable format
     * that can be sent directly to either the client or the server.
     *
     * @return  The messageable format of this instance.
     */
    public String toMessageable(Moshi moshi) {
        if (data instanceof String) {
            return (String) data;
        }

        return moshi
                .adapter(AlisaMessage.class)
                .toJson(this);
    }

}
