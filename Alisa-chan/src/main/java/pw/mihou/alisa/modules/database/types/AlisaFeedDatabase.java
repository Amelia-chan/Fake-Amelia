package pw.mihou.alisa.modules.database.types;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import pw.mihou.alisa.modules.AlisaFeed;
import pw.mihou.alisa.modules.database.AlisaDatabase;

public record AlisaFeedDatabase(MongoClient client) implements AlisaDatabase<AlisaFeed> {

    @Override
    public MongoCollection<Document> collection() {
        return client.getDatabase("amelia").getCollection("feeds");
    }

    @NotNull
    @Override
    public AlisaFeed translate(Document document) {
        return AlisaFeed.from(document);
    }

}
