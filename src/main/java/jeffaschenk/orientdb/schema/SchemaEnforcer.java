package jeffaschenk.orientdb.schema;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import jeffaschenk.orientdb.schema.migrations.DataMigration;

import java.util.List;

/**
 * SchemaEnforcer
 *
 * @author jeffaschenk@gmail.com on 4/4/2016.
 */
public interface SchemaEnforcer {

    /**
     * Phase 1
     *
     * Scan the Domain Model Annotations.
     * @return boolean indicator if Schema is validated or not.
     */
    boolean scanSchema();

    /**
     * Phase 2
     *
     * Validate the existing Schema Against the Current Domain Model.
     * @param db reference to DB Admin Accessor.
     * @return boolean indicator if Schema is validated or not.
     */
    boolean validateSchema(ODatabaseDocumentTx db);

    /**
     * Phase 3
     *
     * Validate the existing Indexes Against the Current Domain Model.
     * @param db reference to DB Admin Accessor.
     * @return boolean indicator if Indexes were validated or not.
     */
    boolean validateIndexes(ODatabaseDocumentTx db);

    /**
     * Phase 3a -- Rebuild Indexes
     * @param db reference to DB Admin Accessor.
     * @return boolean indicator if Indexes were validated or not.
     */
    boolean reBuildIndexes(ODatabaseDocumentTx db);

    /**
     * Phase 4
     *
     * Perform any Data Migrations required for this Implementation or Runtime Environment.
     * @param db reference to DB Admin Accessor.
     * @param dataMigrations References a List of Data Migrations Classes.
     * @return boolean indicator if Data Migrations were successful or not.
     */
    boolean performDataMigrations(ODatabaseDocumentTx db, List<DataMigration> dataMigrations);


}
