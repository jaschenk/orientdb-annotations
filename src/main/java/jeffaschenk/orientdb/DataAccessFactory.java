package jeffaschenk.orientdb;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;

import java.util.List;

/**
 * DataAccessFactory
 */
public interface DataAccessFactory {

    OrientGraph getTx();

    OrientGraphNoTx getNoTx();

    ODatabaseDocumentTx getReader();

    ODatabaseDocumentTx getRoot();

    /**
     * Helper to execute a query as a database reader
     * @param query The query to execute
     * @return The results
     */
    List<ODocument> runQuery(String query);

    /**
     * Helper to execute a query given a specific database context
     * @param query The query to execute
     * @param db A existing database connection
     * @return The results
     */
    List<ODocument> runQuery(String query, ODatabaseDocumentTx db);

}
