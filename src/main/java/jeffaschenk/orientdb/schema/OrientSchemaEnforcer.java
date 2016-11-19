package jeffaschenk.orientdb.schema;


import com.orientechnologies.orient.core.command.script.OCommandScript;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;

import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.index.OIndexManager;
import com.orientechnologies.orient.core.metadata.OMetadata;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.metadata.schema.OType;

import jeffaschenk.orientdb.schema.migrations.DataMigration;
import jeffaschenk.orientdb.annotations.*;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * OrientSchemaEnforcer
 *
 * @author jeffaschenk@gmail.com on 4/1/2016.
 */
@Service
public class OrientSchemaEnforcer implements SchemaEnforcer {
    /**
     * Common Logger
     */
    protected final static org.slf4j.Logger LOGGER =
            LoggerFactory.getLogger(SchemaEnforcer.class);
    /**
     * Constants
     */
    protected static final String OBJECT =
            "Object";

    protected static final String VERTEX =
            "V";

    protected static final String EDGE =
            "E";

    protected static final String ODBProperty_NAME =
            "ODBProperty";

    protected static final String ODBUniqueIdentifier_NAME =
            "ODBUniqueIdentifier";

    protected static final String ODBAssociation_NAME =
            "ODBAssociation";

    protected static final String ODBIndex_NAME =
            "ODBIndex";

    protected static final String UUID_REGEXP =
            "^[0-9A-Za-z]{8}-[0-9A-Za-z]{4}-[0-9A-Za-z]{4}-[0-9A-Za-z]{4}-[0-9A-Za-z]{12}$";

    protected static final String ODB_PHASE_ONE =
            "ODB=> Mapping Phase #1: ";

    protected static final String ODB_PHASE_TWO =
            "ODB=>  Schema Phase #2: ";

    protected static final String ODB_PHASE_THREE =
            "ODB=>   Index Phase #3: ";

    protected static final String ODB_PHASE_FOUR =
            "ODB=>    Data Phase #4: ";

    /**
     * Schema Entity Definitions,
     * built by parsing the specified Entity Package.
     */
    private final List<SchemaEntity> SCHEMA_ENTITY_DEFINITIONS = new ArrayList<>();

    /**
     * Runtime Spring Environment.
     */
    @Autowired
    private Environment environment;

    protected static final String DEFAULT_ENTITY_PACKAGE_PROPERTY_NAME =
            "entity.package";

    /**
     * Phase 1
     * 
     * Validate the existing Schema Against the Current Domain Model.
     *
     * @return boolean indicator if Schema is validated or not.
     */
    @Override
    public synchronized boolean scanSchema() {
        LOGGER.info("{}Performing Schema Scanning of Runtime Domain Model...",
                ODB_PHASE_ONE);
        if (environment.getProperty(DEFAULT_ENTITY_PACKAGE_PROPERTY_NAME)==null) {
            LOGGER.warn("{}No Entity Package Specified, unable to perform Package Scan...",
                    ODB_PHASE_ONE);
            return false;
        }
        /**
         * Parse the Package for the Domain Model.
         */
        if (scanPackage(environment.getProperty(DEFAULT_ENTITY_PACKAGE_PROPERTY_NAME))) {
            /**
             * Now Iterate over our Classes to Determine the Dependency Chin
             * by weighting the Entity.
             */
            LOGGER.info("{}Domain Model Definitions Found: {}", ODB_PHASE_ONE,
                    SCHEMA_ENTITY_DEFINITIONS.size());
            for (SchemaEntity schemaEntity : SCHEMA_ENTITY_DEFINITIONS) {
                /**
                 * Only show Mapped Entities...
                 */
                if (!schemaEntity.isMappedEntity()) {
                    if (LOGGER.isTraceEnabled()) {
                        LOGGER.trace("{}Class: {} Not a Mapped Entity, Ignoring.", ODB_PHASE_ONE,
                                schemaEntity.getEntityClass().getName());
                    }
                    continue;
                }
                /**
                 * Log the Mapped Entity
                 */
                if (LOGGER.isTraceEnabled()) {
                    LOGGER.trace(" + {}", schemaEntity.toString());
                }
                /**
                 * Iterate over Properties...
                 */
                for (String entityName : schemaEntity.getProperties().keySet()) {
                    SchemaEntityProperty schemaEntityProperty = schemaEntity.getProperties().get(entityName);
                    if (LOGGER.isTraceEnabled()) {
                        LOGGER.trace("   ++ {}", schemaEntityProperty.toString());
                    }
                }
            }
            LOGGER.info("{}Domain Model Class Discovery Definition Details: ", ODB_PHASE_ONE);
            /**
             * Weight All Entity Relationships
             */
            weight();
            /**
             * Show Entities in weighted Ordered...
             */
            for (SchemaEntity schemaEntity : SCHEMA_ENTITY_DEFINITIONS) {
                /**
                 * Only show Mapped Entities...
                 */
                if (!schemaEntity.isMappedEntity()) {
                    continue;
                }
                LOGGER.info(" + {}", schemaEntity.toString());
            }
            /**
             * End of Phase One.
             */
            LOGGER.info("{}Successful.", ODB_PHASE_ONE);
            return true;
        } else {
            LOGGER.info("{}Was Not Successful!", ODB_PHASE_ONE);
            return false;
        }
    }

    /**
     * Phase 2
     * 
     * Validate the existing Schema Against the Current Domain Model.
     *
     * @param db reference to DB Admin Accessor.
     * @return boolean indicator if Schema is validated or not.
     */
    @Override
    public synchronized boolean validateSchema(ODatabaseDocumentTx db) {
        LOGGER.info("{}Performing Schema Validation...", ODB_PHASE_TWO);
        /**
         * Initialize our OrientDB Schema Helpers.
         */
        MigrationHelpers helpers = new MigrationHelpersImpl(LOGGER);
        /**
         * Obtain the current Wire Persistent Store's MetaData and in Turn the
         * current Schemata for the database in which we are connected.
         */
        OMetadata metadata = db.getMetadata();
        OSchema schema = metadata.getSchema();
        OIndexManager indexManager = metadata.getIndexManager();
        LOGGER.info("{}Current ODB Class Count: {}", ODB_PHASE_TWO, schema.countClasses());
        /**
         * Provide some debugging if required.
         */
        if (LOGGER.isTraceEnabled()) {
            /**
             * Iterate over the Defined Classes
             */
            for (OClass oClass : schema.getClasses()) {
                LOGGER.trace("{}Defined ODB Class: {}", ODB_PHASE_TWO, oClass.getName());
            }
            /**
             * Iterate over the Defined Indexes
             */
            for (OIndex oIndex : indexManager.getIndexes()) {
                LOGGER.trace("{}Defined ODB Index: {}", ODB_PHASE_TWO, oIndex.getName());
            }
        }
        /**
         * Perform a check to validate we have our Graph Root Classes Defined.
         */
        ensureRootGraphClasses(helpers, schema);
        /**
         * Now Iterate over our Classes to Validate the Class Exists on the current Persistent Store.
         */
        for (SchemaEntity schemaEntity : SCHEMA_ENTITY_DEFINITIONS) {
            /**
             * Only Process Mapped Entities...
             */
            if (!schemaEntity.isMappedEntity()) {
                if (LOGGER.isTraceEnabled()) {
                    LOGGER.trace("{}Class: {} Not a Mapped Entity, Ignoring.", ODB_PHASE_TWO,
                            schemaEntity.getEntityClass().getName());
                }
                continue;
            }
            /**
             * Check Class has been Defined?
             */
            if (helpers.hasClass(schema, schemaEntity.resolveName())) {
                LOGGER.info("{}Class: {} Already Defined.",
                        ODB_PHASE_TWO, schemaEntity.resolveName());
            } else {
                LOGGER.info("{}Class: {} Does not Exist on Persistent Store, Will Define new Class.",
                        ODB_PHASE_TWO, schemaEntity.resolveName());
                /**
                 * Ensure the Class is Defined.
                 */
                defineClass(helpers, schema, schemaEntity);
            }
            /**
             * Validate the Properties for the Class Defined.
             * Associations are not defined until all Classes have been defined...
             */
            validateProperties(helpers, schema, schemaEntity);
        }
        /**
         * Now that all Classes should be defined,
         * Iterate Again to Define and Check all Associations.
         */
        for (SchemaEntity schemaEntity : SCHEMA_ENTITY_DEFINITIONS) {
            /**
             * Only Process Mapped Entities...
             */
            if (!schemaEntity.isMappedEntity()) {
                continue;
            }
            /**
             * Validate the Properties for the Class Defined.
             */
            LOGGER.info("{}Class: {} Validating Associations.",
                    ODB_PHASE_TWO, schemaEntity.resolveName());
            validateAssociations(helpers, schema, schemaEntity);
        }

        /**
         * End of Phase Two.
         */
        LOGGER.info("{}Successful.", ODB_PHASE_TWO);
        return true;
    }

    /**
     * Phase 3
     * 
     * Validate the existing Indexes Against the Current Domain Model.
     *
     * @param db reference to DB Admin Accessor.
     * @return boolean indicator if Indexes is validated or not.
     */
    @SuppressWarnings("unchecked")
    @Override
    public boolean validateIndexes(ODatabaseDocumentTx db) {
        LOGGER.info("{}Performing Index Validation...", ODB_PHASE_THREE);
        int issueCount = 0;
        /**
         * Initialize our OrientDB Schema Helpers.
         */
        MigrationHelpers helpers = new MigrationHelpersImpl(LOGGER);
        /**
         * Obtain the current Wire Persistent Store's MetaData and in Turn the
         * current Schemata for the database in which we are connected.
         */
        OMetadata metadata = db.getMetadata();
        OSchema schema = metadata.getSchema();
        /**
         * Iterate over Classes to Define All Applicable Indices...
         */
        List<String> indicesProcessed = new ArrayList<>();
        for (SchemaEntity schemaEntity : SCHEMA_ENTITY_DEFINITIONS) {
            List<Annotation> odbIndices = (List<Annotation>)
                    schemaEntity.getEntityData().get(ODBIndex_NAME);
            if (odbIndices == null || odbIndices.isEmpty()) {
                continue;
            }
            /**
             * Iterate over the Indices to be defined...
             */
            for (Annotation annotation : odbIndices) {
                String indexName = ((ODBIndex) annotation).name();
                /**
                 * Did we Already Process this Named Index?
                 */
                if (indicesProcessed.contains(indexName)) {
                    continue;
                }
                /**
                 * Continue Accessing Index Definition.
                 */
                String indexSQL = ((ODBIndex) annotation).sql();
                if (indexSQL == null || indexSQL.isEmpty()) {
                    LOGGER.info("{}Index '{}' no SQL Defined for Index Definition, Ignoring!",
                            ODB_PHASE_THREE, indexName);
                    continue;
                }
                // As long as we have the SQL to define the Index we are good...
                //
                //String[] properties = ((ODBIndex)annotation).properties();
                //ODBIndex.EngineType engineType = ((ODBIndex)annotation).engineType();
                //ODBIndex.IndexType indexType = ((ODBIndex)annotation).type();
                /**
                 * Define index...
                 */
                try {
                    OClass oClass = helpers.ensureClass(schema, schemaEntity.getEntityClass().getSimpleName());
                    if (!helpers.hasIndex(oClass, indexName)) {
                        OCommandScript creationCommand = new OCommandScript("sql", indexSQL);
                        db.command(creationCommand).execute();
                        db.commit();
                        LOGGER.info("{}Index '{}' has been Defined.",
                                ODB_PHASE_THREE, indexName);
                    } else {
                        LOGGER.info("{}Index '{}' has already been Defined.",
                                ODB_PHASE_THREE, indexName);
                    }
                } catch (Exception e) {
                    LOGGER.error("{}Index '{}' had Issue Validating: '{}'",
                            ODB_PHASE_THREE, indexName, e.getMessage());
                    issueCount++;
                }
                /**
                 * Add to our Processed List...
                 */
                indicesProcessed.add(indexName);
            }
        }
        /**
         * End of Phase Three.
         */
        if (issueCount == 0) {
            LOGGER.info("{}Successful.", ODB_PHASE_THREE);
            return true;
        } else {
            LOGGER.warn("{}Issues Raised: '{}' while defining Indices.", ODB_PHASE_THREE, issueCount);
            return false;
        }
    }

    /**
     * Phase 3a -- Rebuild Indexes
     * @param db reference to DB Admin Accessor.
     * @return boolean indicator if Indexes were validated or not.
     */
    @Override
    public boolean reBuildIndexes(ODatabaseDocumentTx db) {
        try {
                OCommandScript creationCommand = new OCommandScript("sql", "REBUILD INDEX *");
                db.command(creationCommand).execute();
                db.commit();
                LOGGER.info("{}Successfully Issued Index REBUILD.", ODB_PHASE_THREE);
                return true;
        } catch (Exception e) {
                LOGGER.warn("{}Exception Raised: '{}' while Rebuilding Indices.", ODB_PHASE_THREE, e.getMessage(), e);
                return false;
        }
    }

    /**
     * Phase 4
     * 
     * Perform any Data Migrations required for this Implementation or Runtime Environment.
     *
     * @param dataMigrations References a List of Data Migrations Classes.
     * @return boolean indicator if Data Migrations were successful or not.
     */
    @Override
    public boolean performDataMigrations(ODatabaseDocumentTx db, List<DataMigration> dataMigrations) {
        /**
         * Determine if we have any Data Migrations to be performed or not...
         */
        if (dataMigrations == null || dataMigrations.isEmpty()) {
            LOGGER.info("{}Successful, No Data Migrations were Defined to Run.", ODB_PHASE_FOUR);
            return true;
        }
        /**
         * Iterate over the Data Migrations.
         */
        LOGGER.info("{}Performing Data Migrations...", ODB_PHASE_FOUR);
        for (DataMigration dataMigration : dataMigrations) {
            dataMigration.performDataMigration(db);
        }
        /**
         * End of Phase Four.
         */
        LOGGER.info("{}Successful.", ODB_PHASE_FOUR);
        return true;
    }

    // *****************************************************************
    // Phase Utility Methods
    // *****************************************************************

    /**
     * Scan the contents of a package for ODB specific entity bindings.
     *
     * @param packageName name of the package to scan
     * @return boolean Indicates if this method was successful or not...
     */
    @SuppressWarnings("unchecked")
    protected boolean scanPackage(String packageName) {
        /**
         * Get Classes in Package using Spring Candidate Component Provider.
         */
        LOGGER.info("{}Scanning Package: '{}'", ODB_PHASE_ONE, packageName);
        Integer dependencyId = 0;
        /**
         * Create Scanner and disable default filters (that is the 'false' argument)
         * and add filter to match all classes...
         */
        //final ClassPathScanningCandidateComponentProvider provider =
        //        new ClassPathScanningCandidateComponentProvider(false);
        //provider.addIncludeFilter(new RegexPatternTypeFilter(Pattern.compile(".*")));
        /**
         * Get the Matching Classes from our Candidate Provider.
         */
        // final Set<BeanDefinition> classes = provider.findCandidateComponents(packageName);
        final Set<BeanDefinition> classes = findCandidateComponents(packageName);
        /**
         * Indicate the Number of Classes Found...
         */
        LOGGER.info("{}Entity Classes Found in Package: {}", ODB_PHASE_ONE, classes.size());
        /**
         * Now Iterate over each Entity Class Found.
         */
        String packageNamePrefix = packageName + ".";
        for (BeanDefinition bean : classes) {
            String className = bean.getBeanClassName();
            /**
             * If our Class Name is not with our Entity Packages,
             * then simply Ignore.
             */
            if (!className.startsWith(packageNamePrefix)) {
                continue;
            }
            /**
             * Obtain the Class based Upon the Name,
             * any Errors/Exceptions are Ignored during this Phase.
             */
            Class jClass;
            try {
                jClass = Class.forName(className);
            } catch (Exception ignored) {
                continue;
            }
            if (jClass == null) {
                continue;
            }
            /**
             * Determine if this Class is a ENUM or Interface, which we can Ignore during Processing...
             */
            if (jClass.isEnum() || jClass.isInterface()) {
                continue;
            }
            /**
             * Instantiate a Schema Entity Class for us to Map Our Definitions.
             */
            dependencyId++;
            SchemaEntity schemaEntity = new SchemaEntity(dependencyId, className, jClass);
            SCHEMA_ENTITY_DEFINITIONS.add(schemaEntity);
            /**
             * Check if this Class Abstract?
             */
            schemaEntity.setAbstractClass(Modifier.isAbstract(jClass.getModifiers()));
            /**
             * Get Base Annotations.
             */
            Annotation[] annotations = jClass.getAnnotations();
            for (Annotation annotation : annotations) {
                processClassAnnotation(schemaEntity, annotation);
            }
            /**
             * Process the Inheritance Chain, without inherited fields, since our
             * Persistent Store implementation is OrientDB, polymorphism is
             * automatic, as long as inheritance chain is maintained.
             */
            processInheritance(jClass, schemaEntity);
            /**
             * Is this a Mapped Entity? If so, obtain the Fields aka Properties.
             */
            if (schemaEntity.isMappedEntity()) {
                processFieldAnnotations(jClass, schemaEntity);
            }
        }
        return true;
    }

    /**
     * processInheritance
     * 
     * Recursion Processing helper Method.
     *
     * @param oClass       Entity Class
     * @param schemaEntity Current Schema Entity Being Processed.
     */
    @SuppressWarnings("unchecked")
    protected void processInheritance(Class oClass, SchemaEntity schemaEntity) {
        if (oClass.getSuperclass() == null) {
            return;
        }
        Class sClass = oClass.getSuperclass();
        if (!sClass.getSimpleName().equals(OBJECT)) {
            schemaEntity.getInheritanceChain().add(sClass);
        }
        Annotation[] annotations = sClass.getAnnotations();
        for (Annotation annotation : annotations) {
            processClassAnnotation(schemaEntity, annotation);
        }
        /**
         * Check if this SuperClass is derived from Something other than Object?
         */
        if (sClass.getSuperclass() != null && !sClass.getSimpleName().equals(OBJECT)) {
            processInheritance(sClass, schemaEntity);
        }
    }

    /**
     * Process ODB Specific Annotations
     *
     * @param schemaEntity Current Schema Entity POJO
     * @param annotation   Associated Annotation
     */
    @SuppressWarnings("unchecked")
    protected void processClassAnnotation(SchemaEntity schemaEntity, Annotation annotation) {
        if (annotation instanceof ODBGraphObject) {
            schemaEntity.setMappedEntity(true);
            schemaEntity.setOverrideEntityName(((ODBGraphObject) annotation).name());
            schemaEntity.setGraphType(((ODBGraphObject) annotation).type());
            schemaEntity.setRootClass(((ODBGraphObject) annotation).rootClass());
            schemaEntity.getEntityData().put(annotation.getClass().getSimpleName(), annotation);
        } else if (annotation instanceof ODBIndex) {
            /**
             * Process a Single Index Annotation...
             */
            if (!schemaEntity.getEntityData().containsKey(ODBIndex_NAME)) {
                schemaEntity.getEntityData().put(ODBIndex_NAME, new ArrayList<Annotation>());
            }
            List<Annotation> odbIndices = (List<Annotation>)
                    schemaEntity.getEntityData().get(ODBIndex_NAME);
            odbIndices.add(annotation);
        } else if (annotation instanceof ODBIndices) {
            /**
             * Iterate over the Multiple Defined Indices...
              */
            for(ODBIndex innerAnnotation : ((ODBIndices)annotation).value()) {
                if (!schemaEntity.getEntityData().containsKey(ODBIndex_NAME)) {
                    schemaEntity.getEntityData().put(ODBIndex_NAME, new ArrayList<Annotation>());
                }
                List<Annotation> odbIndices = (List<Annotation>)
                        schemaEntity.getEntityData().get(ODBIndex_NAME);
                odbIndices.add(innerAnnotation);
            }
        }
    }

    /**
     * processFieldAnnotations
     * 
     * Recursion Processing helper Method to obtain Fields for an Entity Object.
     *
     * @param oClass       Entity Class
     * @param schemaEntity Current Schema Entity Being Processed.
     */
    protected void processFieldAnnotations(Class oClass, SchemaEntity schemaEntity) {
        /**
         * Get Fields if Mapped...
         */
        Field[] fields = oClass.getDeclaredFields();
        if (fields == null) {
            return;
        }
        for (Field field : fields) {
            /**
             * Ignore any Statically Defined or Final Fields.
             */
            if (Modifier.isStatic(field.getModifiers()) ||
                    Modifier.isFinal(field.getModifiers())) {
                continue;
            }
            boolean uniqueIdentifier = false;
            Map<String, Object> propertyData = new HashMap<>();
            Annotation[] annotations = field.getAnnotations();
            for (Annotation annotation : annotations) {
                if (annotation instanceof ODBUniqueIdentifier) {
                    uniqueIdentifier = true;
                    propertyData.put(ODBUniqueIdentifier_NAME, annotation);
                } else if (annotation instanceof ODBAssociation) {
                    propertyData.put(ODBAssociation_NAME, annotation);
                } else if (annotation instanceof ODBProperty) {
                    propertyData.put(ODBProperty_NAME, annotation);
                }
            }
            /**
             * Add Property to Mapped Fields.
             */
            SchemaEntityProperty schemaEntityProperty =
                    new SchemaEntityProperty(field.getName(), field.getType(), uniqueIdentifier);
            schemaEntityProperty.getPropertyData().putAll(propertyData);
            schemaEntity.getProperties().put(schemaEntityProperty.getPropertyName(), schemaEntityProperty);
        }
    }

    /**
     * Private Helper Method to Validate the Properties for a Given ODB Class.
     *
     * @param helpers      Reference to Helpers
     * @param schema       Reference to Schemata
     * @param schemaEntity Entity Reference
     */
    protected void validateProperties(MigrationHelpers helpers, OSchema schema, SchemaEntity schemaEntity) {
        /**
         * Iterate over all Schema Entity Properties...
         */
        for (String propertyKey : schemaEntity.getProperties().keySet()) {
            SchemaEntityProperty property = schemaEntity.getProperties().get(propertyKey);
            String propertyName = property.getPropertyName();
            ODBProperty.PropertyType propertyType = null;
            /**
             * Obtain the Annotation for the Property.
             */
            if (property.getPropertyData().get(ODBProperty_NAME) != null) {
                ODBProperty propertyAnnotation = (ODBProperty) property.getPropertyData().get(ODBProperty_NAME);
                if (propertyAnnotation.name() != null && !propertyAnnotation.name().isEmpty()) {
                    propertyName = propertyAnnotation.name();
                }
                propertyType = propertyAnnotation.type();
                if (propertyType != null && propertyType.equals(ODBProperty.PropertyType.TRANSIENT)) {
                    continue;
                }
            } else if (property.getPropertyData().get(ODBUniqueIdentifier_NAME) != null) {
                ODBUniqueIdentifier propertyAnnotation =
                        (ODBUniqueIdentifier) property.getPropertyData().get(ODBUniqueIdentifier_NAME);
                if (propertyAnnotation.name() != null && !propertyAnnotation.name().isEmpty()) {
                    propertyName = propertyAnnotation.name();
                }
                propertyType = ODBProperty.PropertyType.STRING;
            } else if (property.getPropertyData().get(ODBAssociation_NAME) != null) {
                continue;
            }
            /**
             * Process Property or Association?
             */
            if (propertyType != null) {
                /**
                 * Now that we have the correct name if it was overridden, validate the Properties Existence.
                 */
                if (helpers.hasProperty(schema, schemaEntity.resolveName(), propertyName)) {
                    LOGGER.info("{}Class: {}, Property: '{}' Already Defined.",
                            ODB_PHASE_TWO, schemaEntity.resolveName(),
                            propertyName);
                    /**
                     * Even though the Property is Defined, we need to ensure the Property Type is the Same
                     * as what has been defined per Annotations.
                     */

                } else {
                    LOGGER.info("{}Class: {}, Property: '{}' Does Not Exist, need to Define.",
                            ODB_PHASE_TWO, schemaEntity.resolveName(),
                            propertyName);
                }
            }
            /**
             * Define the Property...
             */
            defineProperty(helpers, schema, schemaEntity, property, propertyName);
        }
    }

    /**
     * Private Helper Method to Validate the Associations for a Given ODB Class.
     *
     * @param helpers      Reference to Helpers
     * @param schema       Reference to Schemata
     * @param schemaEntity Entity Reference
     */
    protected void validateAssociations(MigrationHelpers helpers, OSchema schema, SchemaEntity schemaEntity) {
        /**
         * Iterate over all Schema Entity Properties...
         */
        for (String propertyKey : schemaEntity.getProperties().keySet()) {
            SchemaEntityProperty property = schemaEntity.getProperties().get(propertyKey);
            String propertyName = property.getPropertyName();
            ODBAssociation.AssociationType associationType;
            /**
             * Obtain the Annotation for the Association.
             */
            if (property.getPropertyData().get(ODBAssociation_NAME) != null) {
                ODBAssociation associationAnnotation = (ODBAssociation) property.getPropertyData().get(ODBAssociation_NAME);
                if (associationAnnotation.name() != null && !associationAnnotation.name().isEmpty()) {
                    propertyName = associationAnnotation.name();
                }
                associationType = associationAnnotation.type();
                if (associationType != null && associationType.equals(ODBAssociation.AssociationType.TRANSIENT) ||
                        associationType != null && associationType.equals(ODBAssociation.AssociationType.NONE)) {
                    continue;
                }
            } else {
                continue;
            }
            /**
             * Define the Association...
             */
            LOGGER.info("{}Class: {}, Validating Association: '{}'.",
                    ODB_PHASE_TWO, schemaEntity.resolveName(),
                    propertyName);
            defineProperty(helpers, schema, schemaEntity, property, propertyName);
        }
    }

    /**
     * Private Helper Method to Define a Class for a Given ODB Class.
     *
     * @param helpers      Reference to Helpers
     * @param schema       Reference to Schemata
     * @param schemaEntity Entity Reference
     */
    protected void defineClass(MigrationHelpers helpers,
                               OSchema schema, SchemaEntity schemaEntity) {
        /**
         * Ensure the Class has been established with the Correct Setting if the class
         * is Abstract or not.
         */
        OClass oClass = helpers.ensureClass(schema, schemaEntity.resolveName());
        helpers.ensureClassAttribute(oClass, OClass.ATTRIBUTES.ABSTRACT, schemaEntity.isAbstractClass());

        /**
         * Check for a Root Base Class Indicator
         */
        if (schemaEntity.isRootClass() && !schemaEntity.getGraphType().equals(ODBGraphObject.ODBGraphObjectType.NONE)) {
            LOGGER.info("{}Root Class: {} is Derived from: {}",
                    ODB_PHASE_TWO, schemaEntity.resolveName(),
                    schemaEntity.getGraphType().name().substring(0, 1).toUpperCase());
            helpers.ensureClassAttribute(oClass, OClass.ATTRIBUTES.SUPERCLASS,
                    schema.getClass(schemaEntity.getGraphType().name().substring(0, 1).toUpperCase()));
        }
        /**
         * Now Check which SuperClass this is Derived From?
         */
        if (schemaEntity.getInheritanceChain() == null || schemaEntity.getInheritanceChain().isEmpty()) {
            /**
             * No Inheritance Chain so, Ignore, no need for a SuperClass.
             */
            return;
        }
        /**
         * Set our SuperClass per our Inheritance chain.
         */
        Class inheritanceMember = schemaEntity.getInheritanceChain().get(0);
        LOGGER.info("{}Class: {} is Derived from: {}",
                ODB_PHASE_TWO, schemaEntity.resolveName(), inheritanceMember.getName());
        helpers.ensureClassAttribute(oClass, OClass.ATTRIBUTES.SUPERCLASS,
                schema.getClass(inheritanceMember.getSimpleName()));
    }

    /**
     * Private Helper Method to Define a Property for a Given ODB Class.
     *
     * @param helpers      Reference to Helpers
     * @param schema       Reference to Schemata
     * @param schemaEntity Entity Reference
     * @param property     Property reference
     * @param propertyName Property Name Resolved.
     */
    protected void defineProperty(MigrationHelpers helpers,
                                  OSchema schema, SchemaEntity schemaEntity, SchemaEntityProperty property,
                                  String propertyName) {
        /**
         * Validate Property Name is Present.
         */
        if (propertyName == null || propertyName.isEmpty()) {
            LOGGER.error("{}  + Unable to Define Property for Class: {}, As Property Name is Missing!",
                    ODB_PHASE_TWO,
                    schemaEntity.resolveName());
            return;
        }
        /**
         * Obtain our ODB Class from Schema Helper.
         */
        OClass oClass = helpers.ensureClass(schema, schemaEntity.resolveName());
        if (oClass == null) {
            LOGGER.error("{}  + Unable to Obtain ODB Class: {}, very Bad!", ODB_PHASE_TWO,
                    schemaEntity.resolveName());
            return;
        }

        /**
         * Define the Property either simple or Association.
         */
        if (property.getPropertyData().get(ODBProperty_NAME) != null) {
            ODBProperty propertyAnnotation = (ODBProperty) property.getPropertyData().get(ODBProperty_NAME);
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("{}  + Defining Property: '{}'", ODB_PHASE_TWO, propertyName);
            }
            OType oType = SchemaEntityPropertyTransformer.entityPropertyTypeToOType(propertyAnnotation.type());
            if (oType != null) {
                OProperty oProperty = helpers.ensureProperty(oClass, propertyName, oType);
                /**
                 * Define Attributes for the Property if applicable...
                 */
                setCommonAttributes(helpers, oProperty, propertyAnnotation.mandatory(), propertyAnnotation.notNull());
                /**
                 * Define any RegEx Validators, if Applicable...
                 */
                setRegExAttributes(helpers, oProperty, propertyAnnotation.regex());
                /**
                 * If the type is Embedded, add the Linked Class if Applicable.
                 */
                if (oType == OType.EMBEDDED ||
                        oType == OType.EMBEDDEDLIST ||
                        oType == OType.EMBEDDEDMAP ||
                        oType == OType.EMBEDDEDSET) {
                    /**
                     * Create Linked Class Name, if Applicable...
                     */
                    if (propertyAnnotation.linkedClassName() != null && !propertyAnnotation.linkedClassName().isEmpty()) {
                        helpers.ensurePropertyAttribute(oProperty,
                                OProperty.ATTRIBUTES.LINKEDCLASS, schema.getClass(propertyAnnotation.linkedClassName()));
                    }
                    /**
                     * Create Linked Type, if Applicable...
                     */
                    if (propertyAnnotation.linkedType() != null &&
                            !propertyAnnotation.linkedType().equals(ODBProperty.LinkedType.NONE)) {
                        OType linkedTypeOType =
                                SchemaEntityPropertyTransformer.entityLinkedTypeToOType(propertyAnnotation.linkedType());
                        helpers.ensurePropertyAttribute(oProperty, OProperty.ATTRIBUTES.LINKEDTYPE, linkedTypeOType);
                    }

                }
            } else {
                LOGGER.warn("{}  + Unable to Determine OType for Property: '{}', Ignoring.",
                        ODB_PHASE_TWO, propertyName);
            }
            /**
             * Check for ODBUniqueIdentifier Annotation...
             */
        } else if (property.getPropertyData().get(ODBUniqueIdentifier_NAME) != null) {
            ODBUniqueIdentifier propertyAnnotation =
                    (ODBUniqueIdentifier) property.getPropertyData().get(ODBUniqueIdentifier_NAME);
            if (LOGGER.isTraceEnabled()) {
                LOGGER.debug("{}  + Defining Identity Property: '{}'", ODB_PHASE_TWO, propertyName);
            }
            /**
             * Ensure Properties established for Identity
             */
            OType oType = SchemaEntityPropertyTransformer.entityPropertyTypeToOType(ODBProperty.PropertyType.STRING);
            OProperty oProperty = helpers.ensureProperty(oClass, propertyName, oType);
            /**
             * Define Attributes for the Property if applicable...
             */
            setCommonAttributes(helpers, oProperty, propertyAnnotation.mandatory(), propertyAnnotation.notNull());
            /**
             * Define a RegEx to ensure UUID Compliance.
             */
            helpers.ensurePropertyAttribute(oProperty, OProperty.ATTRIBUTES.REGEXP, UUID_REGEXP);
            /**
             * Check for Association Annotation...
             */
        } else if (property.getPropertyData().get(ODBAssociation_NAME) != null) {
            ODBAssociation associationAnnotation = (ODBAssociation) property.getPropertyData().get(ODBAssociation_NAME);
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("{}  + Defining Association: {}", ODB_PHASE_TWO, associationAnnotation.toString());
            }
            /**
             * Check for Edge type of Association Type.
             */
            if (associationAnnotation.type().equals(ODBAssociation.AssociationType.EDGE)) {
                if (associationAnnotation.edgeName() == null || associationAnnotation.edgeName().isEmpty()) {
                    LOGGER.warn("{}  + Edge Association Specified, but no Edge Name Specified, Fix Annotation, Ignoring!",
                            ODB_PHASE_TWO);
                    return;
                }
                /**
                 * Ensure Properties established for Edge
                 */
                OClass edgeCls = helpers.ensureClass(schema, associationAnnotation.edgeName());
                helpers.ensureClassAttribute(edgeCls, OClass.ATTRIBUTES.SUPERCLASS,
                        schema.getClass(EDGE));
                helpers.ensureClassAttribute(edgeCls, OClass.ATTRIBUTES.ABSTRACT, false);

            } else if (associationAnnotation.type().toString().toUpperCase().startsWith("LINK") ||
                    associationAnnotation.type().toString().toUpperCase().startsWith("EMBEDDED")) {
                /**
                 * Handle Links
                 */
                OType oType = SchemaEntityPropertyTransformer.entityAssociationTypeToOType(associationAnnotation.type());
                if (oType != null) {
                    OProperty oProperty = helpers.ensureProperty(oClass, propertyName, oType);
                    /**
                     * Create Linked Class Name
                     */
                    if (associationAnnotation.linkedClassName() != null && !associationAnnotation.linkedClassName().isEmpty()) {
                        helpers.ensurePropertyAttribute(oProperty,
                                OProperty.ATTRIBUTES.LINKEDCLASS, schema.getClass(associationAnnotation.linkedClassName()));
                    }
                    if (associationAnnotation.linkedType() != null &&
                            !associationAnnotation.linkedType().equals(ODBAssociation.LinkedType.NONE)) {
                        OType linkedTypeOType = SchemaEntityPropertyTransformer.entityLinkedTypeToOType(associationAnnotation.linkedType());
                        helpers.ensurePropertyAttribute(oProperty, OProperty.ATTRIBUTES.LINKEDTYPE, linkedTypeOType);
                    }
                    /**
                     * Define Attributes for the Property if applicable...
                     */
                    setCommonAttributes(helpers, oProperty, associationAnnotation.mandatory(), associationAnnotation.notNull());
                } else {
                    LOGGER.warn("{}  + Unable to Determine OType for Association: '{}', Ignoring.",
                            ODB_PHASE_TWO, propertyName);
                }
            } else {
                LOGGER.warn("{}  + Unknown Association Type:'{}', Ignoring!",
                        ODB_PHASE_TWO, associationAnnotation.type().toString());
            }

            /**
             * Show anything not Handled...
             */
        } else if (!property.getPropertyData().isEmpty()) {
            /**
             * Catch All in case we Missed something.
             */
            LOGGER.warn("{}   + Unknown Property Annotation: Dumping Property Data:", ODB_PHASE_TWO);
            /**
             * Dump the Property Data
             */
            for (String key : property.getPropertyData().keySet()) {
                LOGGER.info("{}  + Property Data: '{}' '{}'", ODB_PHASE_TWO,
                        key,
                        property.getPropertyData().get(key));
            }
        } else {
            LOGGER.debug("{}  + Unknown Property: '{}', Unable to Determine how to Define!", ODB_PHASE_TWO, propertyName);
        }

    }

    /**
     * Private Helper Method to ensure The Root Graph Classes are Available.
     *
     * @param helpers Reference
     * @param schema  OSchema Reference
     */
    protected void ensureRootGraphClasses(MigrationHelpers helpers, OSchema schema) {
        /**
         * Perform a check to validate we have our Graph Root CLasses Defined.
         */
        LOGGER.info("{}Checking for Root Graph Classes: {} and {}",
                ODB_PHASE_TWO, VERTEX, EDGE);
        if (!helpers.hasClass(schema, VERTEX)) {
            LOGGER.info("{}Creating Root Graph Class: {}.", ODB_PHASE_TWO, VERTEX);
            helpers.ensureClass(schema, VERTEX);
        } else {
            LOGGER.info("{}Root Graph Class: {}, Already Defined.", ODB_PHASE_TWO, VERTEX);
        }
        if (!helpers.hasClass(schema, EDGE)) {
            LOGGER.info("{}Creating Root Graph Class: {}.", ODB_PHASE_TWO, EDGE);
            helpers.ensureClass(schema, EDGE);
        } else {
            LOGGER.info("{}Root Graph Class: {}, Already Defined.", ODB_PHASE_TWO, EDGE);
        }
    }

    /**
     * Private Helper Method to perform Weighting of the Entity Classes
     * to Determine Processing Dependency Order.
     */
    protected void weight() {
        LOGGER.info("{}Processing Weight Order for Schema Entities...", ODB_PHASE_ONE);
        /**
         * Obtain all Abstract Base Classes...
         */
        for (SchemaEntity schemaEntity : SCHEMA_ENTITY_DEFINITIONS) {
            /**
             * Only Process Mapped Entities...
             */
            if (!schemaEntity.isMappedEntity()) {
                continue;
            }
            /**
             * Is Class Abstract?
             */
            if (schemaEntity.isAbstractClass() && schemaEntity.isRootClass()) {
                schemaEntity.setDependencyWeight(Integer.MAX_VALUE);
            } else if (schemaEntity.isAbstractClass()) {
                schemaEntity.setDependencyWeight(Integer.MAX_VALUE - 1);
            }
            /**
             * Does this Class Extend any Base Classes?
             */
            if (schemaEntity.getInheritanceChain().size() > 0) {
                schemaEntity.setDependencyWeight(
                        schemaEntity.getDependencyWeight() - schemaEntity.getInheritanceChain().size());
            }
        }
        /**
         * Now Sort the Entities by this Weight, in ascending order.
         */
        Comparator<SchemaEntity> weightComparator = Comparator.comparing(SchemaEntity::getDependencyWeight);
        SCHEMA_ENTITY_DEFINITIONS.sort(weightComparator.reversed());
    }

    /**
     * findCandidateComponents
     * Obtained from org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider.
     * 
     * Modified from original, to allow inclusion of Non-Concrete Classes.
     *
     * @param basePackage String of Base Package to find Candidates.
     * @return Set of Bean Definitions.
     */
    protected Set<BeanDefinition> findCandidateComponents(String basePackage) {
        LinkedHashSet candidates = new LinkedHashSet();
        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        MetadataReaderFactory metadataReaderFactory =
                new CachingMetadataReaderFactory(new PathMatchingResourcePatternResolver());
        try {
            String ex = "classpath*:" + this.resolveBasePackage(basePackage) + "/" + "**/*.class";
            Resource[] resources = resourcePatternResolver.getResources(ex);

            for (int ei = 0; ei < resources.length; ei++) {
                Resource resource = resources[ei];
                if (LOGGER.isTraceEnabled()) {
                    LOGGER.trace("Scanning " + resource);
                }

                if (resource.isReadable()) {
                    try {

                        MetadataReader ex1 = metadataReaderFactory.getMetadataReader(resource);
                        ScannedGenericBeanDefinition sbd = new ScannedGenericBeanDefinition(ex1);
                        sbd.setResource(resource);
                        sbd.setSource(resource);

                        candidates.add(sbd);

                    } catch (Throwable var13) {
                        throw new BeanDefinitionStoreException("Failed to read candidate component class: " + resource, var13);
                    }
                } else {
                    LOGGER.trace("Resource Ignored because not readable: " + resource);
                }
            }
            /**
             * Return the Candidates
             */
            return candidates;
        } catch (IOException var14) {
            throw new BeanDefinitionStoreException("I/O failure during classpath scanning", var14);
        }
    }

    /**
     * Resolve Base Package Name.
     *
     * @param basePackage String containing Base Package Name.
     * @return String of resolved Base Package.
     */
    protected String resolveBasePackage(String basePackage) {
        return ClassUtils.convertClassNameToResourcePath(this.environment.resolveRequiredPlaceholders(basePackage));
    }

    /**
     * setCommonAttributes
     * @param helpers Reference to Helpers
     * @param oProperty Property to be modified.
     * @param mandatory indicating if property is Mandatory or not.
     * @param notNull indicating if property is NotNull or not.
     */
    protected void setCommonAttributes(MigrationHelpers helpers,
                                       OProperty oProperty, boolean mandatory, boolean notNull) {
        /**
         * Define Attributes for the Property if applicable...
         */
        helpers.ensurePropertyAttribute(oProperty, OProperty.ATTRIBUTES.MANDATORY, mandatory);
        helpers.ensurePropertyAttribute(oProperty, OProperty.ATTRIBUTES.NOTNULL, notNull);
    }

    /**
     * setRegExAttributes
     * @param helpers Reference to Helpers
     * @param oProperty Property to be modified.
     * @param regex String of the Pattern to be Applied.
     */
    protected void setRegExAttributes(MigrationHelpers helpers,
                                       OProperty oProperty, String regex) {
        /**
         * Define Attributes for the Property if applicable...
         */
        if (regex != null && !regex.isEmpty()) {
            helpers.ensurePropertyAttribute(oProperty, OProperty.ATTRIBUTES.REGEXP, regex);
        }
    }

}
