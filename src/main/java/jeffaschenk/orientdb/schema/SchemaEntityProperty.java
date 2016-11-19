package jeffaschenk.orientdb.schema;

import java.util.HashMap;
import java.util.Map;

/**
 * SchemaEntity
 * Represents a Schema Definition Object.
 *
 * @author jeffaschenk@gmail.com on 4/4/16.
 */
public class SchemaEntityProperty {

    private String propertyName;

    private Class<?> propertyClass;

    private boolean identityProperty;

    private final Map<String, Object> propertyData = new HashMap<>();

    public SchemaEntityProperty(String propertyName, Class<?> propertyClass, boolean identityProperty) {
        this.propertyName = propertyName;
        this.propertyClass = propertyClass;
        this.identityProperty = identityProperty;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public Class<?> getPropertyClass() {
        return propertyClass;
    }

    public boolean isIdentityProperty() {
        return identityProperty;
    }

    public Map<String, Object> getPropertyData() {
        return propertyData;
    }

    @Override
    public String toString() {
        return "Property{" +
                "name='" + propertyName + '\'' +
                ", Class=" + propertyClass +
                ", identityProperty=" + identityProperty +
                '}';
    }
}
