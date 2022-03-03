package pw.mihou.alisa.modules.database.types;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import pw.mihou.alisa.modules.database.AlisaDatabase;

public record AlisaFeedDatabase(MongoClient client) implements AlisaDatabase {

    @Override
    public MongoCollection<Document> collection() {
        return client.getDatabase("amelia").getCollection("feeds");
    }
    
}
