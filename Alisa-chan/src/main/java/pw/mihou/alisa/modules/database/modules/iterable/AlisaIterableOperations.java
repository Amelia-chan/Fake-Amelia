package pw.mihou.alisa.modules.database.modules.iterable;

import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Sorts;
import org.bson.Document;

import java.util.function.Function;

public enum AlisaIterableOperations {

    LATEST(documents -> documents.sort(Sorts.descending("_id"))),
    OLDEST(documents -> documents.sort(Sorts.ascending("_id")));

    private final Function<FindIterable<Document>, FindIterable<Document>> operation;

    AlisaIterableOperations(Function<FindIterable<Document>, FindIterable<Document>> operation) {
        this.operation = operation;
    }

    /**
     * Applies the operation performed by this iterable operation.
     *
     * @param iterable  The iterable to perform the operation on.
     * @return          The updated iterable that was operated.
     */
    public FindIterable<Document> apply(FindIterable<Document> iterable) {
        return operation.apply(iterable);
    }

}
