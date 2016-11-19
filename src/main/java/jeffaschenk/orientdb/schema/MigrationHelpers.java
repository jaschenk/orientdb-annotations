package jeffaschenk.orientdb.schema;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.metadata.schema.OType;

/**
 *
 */
public interface MigrationHelpers {

    
    
    OClass ensureClass(OSchema schema, String className);

    Boolean hasClass(OSchema schema, String className);

    void ensureClassAttribute(OClass cls, OClass.ATTRIBUTES iAttribute, Object iValue);

    Boolean hasProperty(OSchema schema, String className, String propName);

    OProperty ensureProperty(OClass cls, String propName, OType type);

    OProperty ensureProperty(OClass cls, String propName, OType type, OClass iLinkedClass);

    OProperty ensureProperty(OClass cls, String propName, OType type, OType iLinkedType);

    void ensurePropertyAttribute(OProperty prop, OProperty.ATTRIBUTES iAttribute, Object iValue);

    void safeDropProperty(OClass cls, String propName);

    Boolean hasIndex(OClass cls, String indexName);
    
    void log(String message);

}
