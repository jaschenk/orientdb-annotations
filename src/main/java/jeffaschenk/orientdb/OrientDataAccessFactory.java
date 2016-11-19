package jeffaschenk.orientdb;

import com.orientechnologies.common.io.OIOException;
import com.orientechnologies.orient.client.remote.OServerAdmin;
import com.orientechnologies.orient.core.command.script.OCommandScript;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import jeffaschenk.orientdb.schema.SchemaEnforcer;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * OrientDataAccessFactory
 */
@Service
public class OrientDataAccessFactory implements DataAccessFactory,
        ApplicationListener<ContextClosedEvent> {

    /**
     * Common Logger
     */
    protected final static org.slf4j.Logger LOGGER =
            LoggerFactory.getLogger(OrientDataAccessFactory.class);

    /**
     * Internal Indicator
     */
    private static boolean NEW_DATABASE_CREATED = false;

    /**
     * Internal Indicator when used within Ingester Stack.
     */
    private static boolean STACK_RUNNING_INGESTER = false;

    /**
     * Property Constants
     */
    protected static final String DB_URL_PROPERTY_NAME = "dbUrl";

    protected static final String DB_ADMIN_PROPERTY_NAME = "serverAdmin";
    protected static final String DB_ADMIN_PASSWORD_PROPERTY_NAME = "adminPassword";


    /**
     * Runtime Environment Properties
     */
    @Autowired
    private Environment environment;

    /**
     * Writer OrientDB Graph Factory.
     */
    protected static OrientGraphFactory writerFactory;

    /**
     * Reader OrientDB Graph Factory.
     */
    protected static OrientGraphFactory readerFactory;

    /**
     * Schema Enforcer
     */
    @Autowired
    private SchemaEnforcer schemaEnforcer;

    /**
     * Initialization
     * Entered when Bean is initialized.
     */
    @PostConstruct
    public void initialization() {
        LOGGER.info("Initialization of ODB DataAccess Factory Implementation Starting...");
        /**
         * Obtain our Properties to Bootstrap Instance.
         */
        if (environment == null) {
            LOGGER.error("Unable to obtain our Runtime Environment, unable to Initialize very Bad!");
            return;
        }

        /**
         * Now Validate Connection
         */
        checkDbConnection();

        /**
         * Setup Factory Pool.
         */
        writerFactory = new OrientGraphFactory(environment.getProperty(DB_URL_PROPERTY_NAME),
                environment.getProperty(DB_ADMIN_PROPERTY_NAME),
                environment.getProperty(DB_ADMIN_PASSWORD_PROPERTY_NAME)).setupPool(1, 64);

        /**
         * Setup Factory Pool.
         */
        readerFactory = new OrientGraphFactory(environment.getProperty(DB_URL_PROPERTY_NAME),
                environment.getProperty(DB_ADMIN_PROPERTY_NAME),
                environment.getProperty(DB_ADMIN_PASSWORD_PROPERTY_NAME)).setupPool(1, 64);
        /**
         * Now Validate Schemata
         *
         * Phase One, Parse the Existing Default Domain Model from Java Code.
         */
        schemaEnforcer.scanSchema();
        /**
         * Phase Two, Now have Schema Checker Validate our existing Schemata and
         * apply any necessary updates if necessary.
         */
        try (ODatabaseDocumentTx db = getRoot()) {
            schemaEnforcer.validateSchema(db);
        }
        /**
         * Phase Three, Now have Schema Checker Validate our existing Indexes and
         * apply any necessary updates if necessary.
         */
        try (ODatabaseDocumentTx db = getRoot()) {
            schemaEnforcer.validateIndexes(db);
            /**
             * If we just created a new database, rebuilding our indexes...
             */
            if (NEW_DATABASE_CREATED) {
                schemaEnforcer.reBuildIndexes(db);
            }
        }
        /**
         * Phase Four, Now have Schema Checker perform any Applicable Data Migrations.
         * apply any necessary updates if necessary.
         */
        try (ODatabaseDocumentTx db = getRoot()) {
            schemaEnforcer.performDataMigrations(db, new ArrayList<>(0));
        }

        /**
         * Show Initialization Status
         */
        LOGGER.info("Initialization of ODB DataAccess Factory Implementation " +
                        "has been wired into runtime Environment using dbUrl:[{}]",
                environment.getProperty(DB_URL_PROPERTY_NAME));
    }

    /**
     * destroyBean
     * Entered when Bean is being destroyed or torn down from the runtime Environment.
     * Simple indicate destruction...
     */
    @PreDestroy
    public void destroyBean() {
        LOGGER.info("ODB DataAccess Factory Implementation has been removed from the runtime Environment.");
        /**
         * Close our Factory Pools.
         */
        writerFactory.close();
        readerFactory.close();
    }

    @Override
    public OrientGraph getTx() {
        return writerFactory.getTx();
    }

    @Override
    public OrientGraphNoTx getNoTx() {
        return readerFactory.getNoTx();
    }

    @Override
    public ODatabaseDocumentTx getRoot() {
        if (STACK_RUNNING_INGESTER) {
            return null;
        } else {
            return new ODatabaseDocumentTx(environment.getProperty(DB_URL_PROPERTY_NAME)).
                    open(environment.getProperty(DB_ADMIN_PROPERTY_NAME),
                            environment.getProperty(DB_ADMIN_PASSWORD_PROPERTY_NAME));
        }
    }

    @Override
    public ODatabaseDocumentTx getReader() {
        if (STACK_RUNNING_INGESTER) {
            return null;
        } else {
            return new ODatabaseDocumentTx(environment.getProperty(DB_URL_PROPERTY_NAME)).
                    open(environment.getProperty(DB_ADMIN_PROPERTY_NAME),
                            environment.getProperty(DB_ADMIN_PASSWORD_PROPERTY_NAME));
        }
    }


    @Override
    public List<ODocument> runQuery(String queryString) {
        return runQuery(queryString, getReader());
    }

    @Override
    public List<ODocument> runQuery(String queryString, ODatabaseDocumentTx db) {
        OSQLSynchQuery<ODocument> oQuery = new OSQLSynchQuery<>(queryString);
        return db.command(oQuery).execute();
    }

    /**
     * checkDbConnection
     * Private Helper to Check our DB Connection
     */
    private void checkDbConnection() {
        /**
         * Check DB Exists, if not create it...
         */
        OServerAdmin oAdmin = null;
        try {
            LOGGER.info("Checking for database: {}", environment.getProperty(DB_URL_PROPERTY_NAME));
            oAdmin = new OServerAdmin(environment.getProperty(DB_URL_PROPERTY_NAME));
            oAdmin.connect(environment.getProperty(DB_ADMIN_PROPERTY_NAME),
                    environment.getProperty(DB_ADMIN_PASSWORD_PROPERTY_NAME));
            Boolean dbExists = oAdmin.existsDatabase();
            if (!dbExists) {
                /**
                 * Here we instantiate the OrientDB Database as a Graph database instance.
                 * We used to instantiate as a Document database instance, prior to using graph API.
                 */
                LOGGER.info("Database '{}' not found. Creating Database.", environment.getProperty(DB_URL_PROPERTY_NAME));

                oAdmin.createDatabase(getDbNameFromUrl(environment.getProperty(DB_URL_PROPERTY_NAME)), "graph", "plocal");
                NEW_DATABASE_CREATED = true;
            } else {
                LOGGER.info("Database '{}' found.", environment.getProperty(DB_URL_PROPERTY_NAME));
            }
        } catch (IOException ex) {
            LOGGER.warn("IO error connecting to OrientDB database. Exiting");
            System.exit(1);
        } catch (OIOException ex) {
            LOGGER.error("Unable to connect to OrientDB instance. Please make sure OrientDB is running. Exiting.");
            System.exit(1);
        } finally {
            if (oAdmin != null && oAdmin.isConnected()) {
                oAdmin.close();
            }
        }

    }

    /**
     * getDbNameFromUrl
     *
     * @param dbUrl to obtain Database name from.
     * @return String of parsed Database Name.
     */
    private String getDbNameFromUrl(String dbUrl) {
         if (dbUrl == null || dbUrl.isEmpty() || !dbUrl.contains("/")) {
             throw new IllegalArgumentException("");
         }
         return dbUrl.substring(dbUrl.indexOf("/"));
    }

    /**
     * onApplicationEvent
     *
     * @param e Reference to ContextClosedEvent
     */
    @Override
    public void onApplicationEvent(ContextClosedEvent e) {
        LOGGER.info("Closing database connection pools");
    }

}
