package jeffaschenk.orientdb.schema.migrations;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;

/**
 * DataMigration
 *
 * @author jeff.schenk
 */
public interface DataMigration {

    /**
     * Perform the Necessary Data Migrations...
     * @param db Reference to ODatabase Document Accessor.
     */
    void performDataMigration(ODatabaseDocumentTx db);

}
