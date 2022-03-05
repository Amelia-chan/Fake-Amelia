package pw.mihou.alisa.modules.database.modules.iterable;

import com.mongodb.Function;
import com.mongodb.client.FindIterable;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public record AlisaIterable<Type>(
        FindIterable<Document> iterable,
        List<AlisaIterableOperations> operations,
        Function<Document, Type> mapper
) {

    /**
     * Updates the {@link AlisaIterable} to include a specific iterable operation.
     *
     * @param operation The operation to include.
     * @return          The {@link AlisaIterable} for chain-calling methods.
     */
    public AlisaIterable<Type> addOperation(AlisaIterableOperations operation) {
        operations.add(operation);
        return this;
    }

    /**
     * Gets the iterable with all the operations applied.
     *
     * @return  The updated {@link FindIterable} instance.
     */
    public AlisaIterable<Type> apply() {
        AtomicReference<FindIterable<Document>> appliedIterable = new AtomicReference<>(iterable);
        operations.forEach(operation -> appliedIterable.getAndUpdate(operation::apply));

        return new AlisaIterable<Type>(appliedIterable.get(), Collections.emptyList(), mapper);
    }

    /**
     * Iterates through the {@link FindIterable} and collects them into
     * a single unmodifiable {@link List}.
     *
     * @return  The list to collect.
     */
    public List<Document> toList() {
        return StreamSupport.stream(iterable.spliterator(), false).toList();
    }

    /**
     * Maps all the {@link Document}s into their intended type before
     * producing a list of them. A short-hand expression of {@link this#map()} and
     * {@link Stream#toList()}.
     *
     * @return  A list of all the documents mapped into their type.
     */
    public List<Type> mapAndList() {
        return map().toList();
    }

    /**
     * Maps the {@link Document}s of this iterable into its intended
     * type by using a mapping function.
     *
     * @param mapper    The mapper to use to map the document into the specific type.
     * @param <T>       The type to map the documents into.
     * @return          A stream of all the documents mapped into the specific type.
     */
    public <T> Stream<T> map(Function<Document, T> mapper) {
        return StreamSupport.stream(iterable.spliterator(), false).map(mapper::apply);
    }

    /**
     * Maps the {@link Document}s of this iterable into its intended type
     * that was provided during the creation of this mapper.
     *
     * @return  A stream of all the documents mapped into the specific type.
     */
    public Stream<Type> map() {
        return map(this.mapper);
    }

}
