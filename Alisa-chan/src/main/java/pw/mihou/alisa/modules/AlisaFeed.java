package pw.mihou.alisa.modules;

import org.bson.Document;
import pw.mihou.alisa.interfaces.DatabaseModel;
import pw.mihou.alisa.modules.database.modules.AlisaIndex;

import java.util.Date;
import java.util.List;

public record AlisaFeed(int id, long unique, String name, long server,
                        long channel, long user, String url, List<Long> mentions,
                        Date date, AlisaIndex index) implements DatabaseModel {

    /**
     * Creates a new replica of this {@link AlisaFeed} instance but with
     * a field that is different.
     *
     * @param date  The new date of this feed.
     * @return      The near-perfect replica of this model.
     */
    public AlisaFeed date(Date date) {
        return new AlisaFeed(
                id, unique, name, server,
                channel, user, url, mentions,
                date, index
        );
    }

    /**
     * Creates a new replica of this {@link AlisaFeed} instance but with
     * a field that is different.
     *
     * @param user  The new user of this feed.
     * @return      The near-perfect replica of this model.
     */
    public AlisaFeed user(long user) {
        return new AlisaFeed(
                id, unique, name, server,
                channel, user, url, mentions,
                date, index
        );
    }

    /**
     * Creates a new replica of this {@link AlisaFeed} instance but with
     * a field that is different.
     *
     * @param mentions  The new mentions of this feed.
     * @return          The near-perfect replica of this model.
     */
    public AlisaFeed mentions(List<Long> mentions) {
        return new AlisaFeed(
                id, unique, name, server,
                channel, user, url, mentions,
                date, index
        );
    }

    /**
     * Creates a new replica of this {@link AlisaFeed} instance but with
     * a field that is different.
     *
     * @param channel  The new channel of this feed.
     * @return         The near-perfect replica of this model.
     */
    public AlisaFeed channel(long channel) {
        return new AlisaFeed(
                id, unique, name, server,
                channel, user, url, mentions,
                date, index
        );
    }

    /**
     * Creates a new instance from the {@link Document} collected.
     *
     * @param document  The document to reference from.
     * @return          The new model from the data generated.
     */
    public static AlisaFeed from(Document document) {
        return new AlisaFeed(
                document.getInteger("id"),
                document.getLong("unique"),
                document.getString("name"),
                document.getLong("server"),
                document.getLong("channel"),
                document.getLong("user"),
                document.getString("url"),
                document.getList("mentions", Long.class),
                document.getDate("date"),
                new AlisaIndex("unique", document.getLong("unique"))
        );
    }

    @Override
    public Document document() {
        return new Document().append("id", id)
                .append("unique", unique)
                .append("name", name)
                .append("server", server)
                .append("channel", channel)
                .append("user", user)
                .append("url", url)
                .append("mentions", mentions)
                .append("date", date);
    }

    @Override
    public AlisaIndex index() {
        return index;
    }
}
