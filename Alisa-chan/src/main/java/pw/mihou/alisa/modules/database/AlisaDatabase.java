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
import org.jetbrains.annotations.NotNull;
import pw.mihou.alisa.interfaces.DatabaseModel;
import pw.mihou.alisa.modules.database.modules.AlisaField;
import pw.mihou.alisa.modules.database.modules.AlisaIndex;
import pw.mihou.alisa.modules.database.modules.iterable.AlisaIterable;
import pw.mihou.alisa.modules.database.modules.iterable.AlisaIterableOperations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface AlisaDatabase<Type> {

    /**
     * Gets the collection for this database.
     *
     * @return  The collection of this database.
     */
    MongoCollection<Document> collection();

    /**
     * Translates a {@link Document} into a {@link Type} that is intended for this database.
     *
     * @param document  The document to translate.
     * @return          The {@link Type} instance of the document.
     */
    @NotNull
    Type translate(Document document);

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
     * @return      The received {@link Type} form the database if present.
     */
    default CompletableFuture<Optional<Type>> get(AlisaIndex index) {
        return CompletableFuture.supplyAsync(() -> {
            Document document = collection().find(
                    Filters.eq(index.key(), index.value())
            ).first();

            if (document == null) {
                return Optional.empty();
            }

            return Optional.of(translate(document));
        });
    }

    /**
     * Gets all documents that match the index.
     *
     * @param index The index to use when querying the database.
     * @return      The received {@link Document} form the database if present.
     */
    default AlisaIterable<Type> all(AlisaIndex index) {
        return new AlisaIterable<Type>(collection().find(
                Filters.eq(index.key(), index.value())
        ), new ArrayList<>(), this::translate);
    }

    /**
     * Gets all documents that match the indexes.
     *
     * @param indexes   The indexes to use when querying the database.
     * @return          The received {@link Document} form the database if present.
     */
    default AlisaIterable<Type> and(AlisaIndex... indexes) {
        return new AlisaIterable<>(collection().find(
                Filters.and(Arrays.stream(indexes).map(index -> Filters.eq(index.key(), index.value())).toList())
        ), new ArrayList<>(),  this::translate);
    }

    /**
     * Gets all documents that match the query.
     *
     * @param field     The field to find the data from.
     * @param values    The values that a document should match once.
     * @return          The received {@link Document} form the database if present.
     */
    default AlisaIterable<Type> all(String field, Object... values) {
        return new AlisaIterable<>(collection().find(
                Filters.all(field, values)
        ), new ArrayList<>(), this::translate);
    }

    /**
     * Gets all documents that match the indexes.
     *
     * @param indexes   The indexes to use when querying the database.
     * @return          The received {@link Document} form the database if present.
     */
    default AlisaIterable<Type> or(AlisaIndex... indexes) {
        return or(Arrays.stream(indexes).map(index -> Filters.eq(index.key(), index.value())).toArray(Bson[]::new));
    }

    /**
     * Gets all documents that match the filters.
     *
     * @param filters   The filters to use when querying the database.
     * @return          The received {@link Document} form the database if present.
     */
    default AlisaIterable<Type> or(Bson... filters) {
        return new AlisaIterable<>(collection().find(
                Filters.and(filters)
        ), new ArrayList<>(), this::translate);
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
    default AlisaIterable<Type> all() {
        return new AlisaIterable<>(collection().find(), new ArrayList<>(), this::translate);
    }

    /**
     * Sorts all the data by the latest order.
     *
     * @return  An iterable that is sorted by the latest order.
     */
    default AlisaIterable<Type> latest() {
        return all().addOperation(AlisaIterableOperations.LATEST).apply();
    }

    /**
     * Sorts all the data by the latest order.
     *
     * @param iterable  The iterable to use for sorting the data.
     * @return  An iterable that is sorted by the latest order.
     */
    default AlisaIterable<Type> latest(FindIterable<Document> iterable) {
        return new AlisaIterable<Type>(iterable, new ArrayList<>(), this::translate).addOperation(AlisaIterableOperations.LATEST);
    }

    /**
     * Sorts all the data by the oldest order.
     *
     * @param iterable  The iterable to use for sorting the data.
     * @return  An iterable that is sorted by the oldest order.
     */
    default AlisaIterable<Type> oldest(FindIterable<Document> iterable) {
        return new AlisaIterable<>(iterable, new ArrayList<>(), this::translate).addOperation(AlisaIterableOperations.OLDEST);
    }


    /**
     * Sorts all the data by the oldest order.
     *
     * @return  An iterable that is sorted by the oldest order.
     */
    default AlisaIterable<Type> oldest() {
        return all().addOperation(AlisaIterableOperations.OLDEST);
    }

}
