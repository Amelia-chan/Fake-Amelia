package pw.mihou.alisa.modules.database;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.conversions.Bson;
import pw.mihou.alisa.interfaces.DatabaseModel;
import pw.mihou.alisa.modules.database.modules.AlisaField;
import pw.mihou.alisa.modules.database.modules.AlisaIndex;
import pw.mihou.alisa.modules.database.modules.iterable.AlisaIterable;
import pw.mihou.alisa.modules.database.modules.iterable.AlisaIterableOperations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface AlisaDatabase {

    /**
     * Gets the collection for this database.
     *
     * @return  The collection of this database.
     */
    MongoCollection<Document> collection();

    /**
     * Upserts the model onto the database.
     *
     * @param model The model to upsert to the database.
     * @return      The result of upserting to the database.
     */
    default CompletableFuture<UpdateResult> upsert(DatabaseModel model) {
        return CompletableFuture.supplyAsync(() -> collection()
                .replaceOne(
                        Filters.eq(model.index().key(), model.index().value()),
                        model.document(),
                        new ReplaceOptions().upsert(true)
                ));
    }

    /**
     * Gets a specific document from the database.
     *
     * @param index The index to use when querying the database.
     * @return      The received {@link Document} form the database if present.
     */
    default CompletableFuture<Optional<Document>> get(AlisaIndex index) {
        return CompletableFuture.supplyAsync(() ->  Optional.ofNullable(collection().find(
                Filters.eq(index.key(), index.value())
        ).first()));
    }

    /**
     * Gets all documents that match the index.
     *
     * @param index The index to use when querying the database.
     * @return      The received {@link Document} form the database if present.
     */
    default CompletableFuture<AlisaIterable> all(AlisaIndex index) {
        return CompletableFuture.supplyAsync(() ->  new AlisaIterable(collection().find(
                Filters.eq(index.key(), index.value())
        ), new ArrayList<>()));
    }

    /**
     * Gets all documents that match the indexes.
     *
     * @param indexes   The indexes to use when querying the database.
     * @return          The received {@link Document} form the database if present.
     */
    default CompletableFuture<AlisaIterable> and(AlisaIndex... indexes) {
        return CompletableFuture.supplyAsync(() ->  new AlisaIterable(collection().find(
                Filters.and(Arrays.stream(indexes).map(index -> Filters.eq(index.key(), index.value())).toList())
        ), new ArrayList<>()));
    }

    /**
     * Gets all documents that match the query.
     *
     * @param field     The field to find the data from.
     * @param values    The values that a document should match once.
     * @return          The received {@link Document} form the database if present.
     */
    default CompletableFuture<AlisaIterable> all(String field, Object... values) {
        return CompletableFuture.supplyAsync(() ->  new AlisaIterable(collection().find(
                Filters.all(field, values)
        ), new ArrayList<>()));
    }

    /**
     * Gets all documents that match the indexes.
     *
     * @param indexes   The indexes to use when querying the database.
     * @return          The received {@link Document} form the database if present.
     */
    default CompletableFuture<AlisaIterable> or(AlisaIndex... indexes) {
        return or(Arrays.stream(indexes).map(index -> Filters.eq(index.key(), index.value())).toArray(Bson[]::new));
    }

    /**
     * Gets all documents that match the filters.
     *
     * @param filters   The filters to use when querying the database.
     * @return          The received {@link Document} form the database if present.
     */
    default CompletableFuture<AlisaIterable> or(Bson... filters) {
        return CompletableFuture.supplyAsync(() ->  new AlisaIterable(collection().find(
                Filters.and(filters)
        ), new ArrayList<>()));
    }

    /**
     * Deletes a specific document from the database.
     *
     * @param model The model to delete.
     * @return      The result from deleting the document.
     */
    default CompletableFuture<DeleteResult> delete(DatabaseModel model) {
        return CompletableFuture.supplyAsync(() -> collection().deleteOne(
                Filters.eq(model.index().key(), model.index().value())
        ));
    }

    /**
     * Updates one field of the specific database model.
     *
     * @param index The index of the model.
     * @param field The field to update on the model.
     * @return      The result from updating the model.
     */
    default CompletableFuture<UpdateResult> updateField(AlisaIndex index, AlisaField field) {
        return CompletableFuture.supplyAsync(() -> collection().updateOne(
                Filters.eq(index.key(), index.value()),
                Updates.set(field.key(), field.value())
        ));
    }

    /**
     * Gets all the data of the collection.
     *
     * @return  All the data of the collection.
     */
    default CompletableFuture<AlisaIterable> all() {
        return CompletableFuture.supplyAsync(() -> new AlisaIterable(collection().find(), new ArrayList<>()));
    }

    /**
     * Sorts all the data by the latest order.
     *
     * @return  An iterable that is sorted by the latest order.
     */
    default CompletableFuture<AlisaIterable> latest() {
        return all().thenApply(iterable -> iterable.addOperation(AlisaIterableOperations.LATEST).apply());
    }

    /**
     * Sorts all the data by the latest order.
     *
     * @parma iterable  The iterable to use for sorting the data.
     * @return  An iterable that is sorted by the latest order.
     */
    default AlisaIterable latest(FindIterable<Document> iterable) {
        return new AlisaIterable(iterable, new ArrayList<>()).addOperation(AlisaIterableOperations.LATEST);
    }

    /**
     * Sorts all the data by the oldest order.
     *
     * @parma iterable  The iterable to use for sorting the data.
     * @return  An iterable that is sorted by the oldest order.
     */
    default AlisaIterable oldest(FindIterable<Document> iterable) {
        return new AlisaIterable(iterable, new ArrayList<>()).addOperation(AlisaIterableOperations.OLDEST);
    }


    /**
     * Sorts all the data by the oldest order.
     *
     * @return  An iterable that is sorted by the oldest order.
     */
    default CompletableFuture<AlisaIterable> oldest() {
        return all().thenApply(iterable -> iterable.addOperation(AlisaIterableOperations.OLDEST));
    }

}
