package jeffaschenk.orientdb.schema;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.metadata.schema.OType;
import org.slf4j.Logger;

import java.util.Objects;

final class MigrationHelpersImpl implements MigrationHelpers {

    private final Logger logger;

    public MigrationHelpersImpl(Logger logger) {
        this.logger = logger;
    }

    @Override
    public Boolean hasClass(OSchema schema, String className) {
        return schema.existsClass(className);
    }

    @Override
    public OClass ensureClass(OSchema schema, String className) {
        return schema.getOrCreateClass(className);
    }

    @Override
    public OProperty ensureProperty(OClass cls, String propName, OType type) {
        OProperty prop;
        if (cls.existsProperty(propName)) {
            prop = cls.getProperty(propName);
            if(prop.getType() != type) {
                logger.warn("'{}.{}' is not the right type. Changing from '{}' to '{}'", cls.getName(), propName, prop.getType(), type);
                prop.setType(type);
            }
        } else {
            prop = cls.createProperty(propName, type);
            logger.info("Create '{}.{}' property in database.", cls.getName(), propName);
        }

        return prop;
    }

    @Override
    public Boolean hasIndex(OClass cls, String indexName)
    {
        return cls.getClassIndex(indexName) != null;
    }

    @Override
    public Boolean hasProperty(OSchema schema, String className, String propName) {
        OClass oClass = schema.getClass(className);
        if (oClass == null) {
            return false;
        }
        return oClass.existsProperty(propName);
    }

    @Override
    public OProperty ensureProperty(OClass cls, String propName, OType type, OClass iLinkedClass) {
        OProperty prop = ensureProperty(cls, propName, type);
        ensurePropertyAttribute(prop, OProperty.ATTRIBUTES.LINKEDCLASS, iLinkedClass);
        return prop;
    }

    @Override
    public OProperty ensureProperty(OClass cls, String propName, OType type, OType iLinkedType) {
        OProperty prop = ensureProperty(cls, propName, type);
        ensurePropertyAttribute(prop, OProperty.ATTRIBUTES.LINKEDTYPE, iLinkedType);
        return prop;
    }

    @Override
    public void ensurePropertyAttribute(OProperty prop, OProperty.ATTRIBUTES iAttribute, Object expected) {
        final Object actual = prop.get(iAttribute);
        if(!Objects.equals(actual, expected)) {
            logger.warn("Changing '{}' property attribute '{}' from '{}' to '{}'", prop.getName(), iAttribute, actual, expected);
            prop.set(iAttribute, expected);
        }
    }

    @Override
    public void ensureClassAttribute(OClass cls, OClass.ATTRIBUTES iAttribute, Object iValue) {
        final Object actual = cls.get(iAttribute);
        if(!Objects.equals(actual, iValue)) {
            logger.warn("Changing '{}' class attribute '{}' from '{}' to '{}'", cls.getName(), iAttribute, actual, iValue);
            cls.set(iAttribute, iValue);
        }
    }

    @Override
    public void safeDropProperty(OClass cls, String propName) {
        if(cls.existsProperty(propName)) {
            logger.warn("Dropping '{}.{}' property; should not exist.", cls.getName(), propName);
            cls.dropProperty(propName);
        }
    }

    @Override
    public void log(String message) {
        logger.info(message);
    }
}
