package pw.mihou.alisa.modules.database;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCompressor;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import pw.mihou.alisa.modules.http.AlisaHttpCall;

import java.util.List;

public class AlisaDatabaseClient {

    private final MongoClient client;

    /**
     * Creates a new {@link MongoClient} and performs a warm startup
     * on the {@link MongoClient}.
     *
     * @param uri   The URI of the {@link MongoClient}.
     */
    public AlisaDatabaseClient(String uri) {
        this.client = MongoClients.create(
                MongoClientSettings.builder()
                        .applicationName(AlisaHttpCall.USER_AGENT)
                        .retryWrites(true)
                        .retryReads(true)
                        .compressorList(List.of(
                                MongoCompressor.createZstdCompressor()
                        ))
                        .applyConnectionString(new ConnectionString(
                                uri
                        ))
                        .build()
        );

        // Drop the database names  on the loop, this is just to cold-start the database.
        client.listDatabaseNames().forEach(s -> {});
    }

    /**
     * Gets the {@link MongoClient} associated with this client.
     *
     * @return  The {@link MongoClient} interface.
     */
    public MongoClient client() {
        return client;
    }

    /**
     * Gets the {@link MongoDatabase} that links with the database name
     * specified using the {@link MongoClient} associated.
     *
     * @param name  The name of the database.
     * @return      The {@link MongoDatabase} interface.
     */
    public MongoDatabase database(String name) {
        return client.getDatabase(name);
    }

    /**
     * Gets the {@link MongoCollection} that links with the collection name
     * specified and under the specified database name using the {@link MongoClient} associated.
     *
     * @param database      The name of the database.
     * @param collection    The name of the collection.
     * @return      The {@link com.mongodb.client.MongoCollection} interface.
     */
    public MongoCollection<Document> collection(String database, String collection) {
        return client.getDatabase(database).getCollection(collection);
    }

}
