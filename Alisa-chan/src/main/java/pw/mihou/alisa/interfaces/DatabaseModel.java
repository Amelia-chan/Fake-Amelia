package pw.mihou.alisa.interfaces;

import org.bson.Document;
import pw.mihou.alisa.modules.database.modules.AlisaIndex;

public interface DatabaseModel {

    /**
     * Writes all the data into the {@link Document} which can be sent directly to the database.
     *
     * @return The document written.
     */
    Document document();

    /**
     * Gets the unique key-value for this model which is used as an index to speed up
     * requests and also to upsert data onto the database.
     *
     * @return  The unique index for this instance.
     */
    AlisaIndex index();

}
