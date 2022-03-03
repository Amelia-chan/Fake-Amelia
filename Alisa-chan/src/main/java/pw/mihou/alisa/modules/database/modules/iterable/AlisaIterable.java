package pw.mihou.alisa.modules.database.modules.iterable;

import com.mongodb.client.FindIterable;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public record AlisaIterable(
        FindIterable<Document> iterable,
        List<AlisaIterableOperations> operations
) {

    /**
     * Updates the {@link AlisaIterable} to include a specific iterable operation.
     *
     * @param operation The operation to include.
     * @return          The {@link AlisaIterable} for chain-calling methods.
     */
    public AlisaIterable addOperation(AlisaIterableOperations operation) {
        operations.add(operation);
        return this;
    }

    /**
     * Gets the iterable with all the operations applied.
     *
     * @return  The updated {@link FindIterable} instance.
     */
    public AlisaIterable apply() {
        AtomicReference<FindIterable<Document>> appliedIterable = new AtomicReference<>(iterable);
        operations.forEach(operation -> appliedIterable.getAndUpdate(operation::apply));

        return new AlisaIterable(appliedIterable.get(), Collections.emptyList());
    }

    /**
     * Iterates through the {@link FindIterable} and collects them into
     * a single unmodifiable {@link List}.
     *
     * @return  The list to collect.
     */
    public List<Document> toList() {
        List<Document> documents = new ArrayList<>();
        iterable.forEach(documents::add);

        return Collections.unmodifiableList(documents);
    }

}
