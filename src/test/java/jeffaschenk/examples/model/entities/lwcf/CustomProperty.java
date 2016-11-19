package jeffaschenk.examples.model.entities.lwcf;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jeffaschenk.orientdb.annotations.ODBGraphObject;
import jeffaschenk.orientdb.annotations.ODBIndex;
import jeffaschenk.orientdb.annotations.ODBProperty;
import jeffaschenk.examples.model.entities.RootEntity;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * CustomProperty
 *
 * Example Pojo to Demonstrate OrientDB Schema Annotations.
 *
 * @author jeffaschenk@gmail on 3/19/2016.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@ODBGraphObject(type = ODBGraphObject.ODBGraphObjectType.VERTEX)
@ODBIndex(name = "CustomProperty.ownerUUID",
        engineType = ODBIndex.EngineType.SBTREE,
        sql = "CREATE INDEX CustomProperty.ownerUUID ON CustomProperty (ownerUUID) " +
                "NOTUNIQUE METADATA {ignoreNullValues : false}")
public class CustomProperty extends RootEntity implements Serializable {
    @NotNull
    @ODBProperty(type = ODBProperty.PropertyType.UUID, mandatory = true, notNull = true)
    private String ownerUUID;

    @NotNull
    @ODBProperty(type = ODBProperty.PropertyType.EMBEDDEDMAP)
    private Map<String,Object> domainLocation;

    @NotNull
    @ODBProperty(type = ODBProperty.PropertyType.INTEGER)
    private int domainLocationOrder;

    @NotNull
    @ODBProperty(type = ODBProperty.PropertyType.STRING)
    private String type;

    @NotNull
    @ODBProperty(type = ODBProperty.PropertyType.EMBEDDEDMAP)
    private Map<String,Object> value;

    @NotNull
    @ODBProperty(type = ODBProperty.PropertyType.EMBEDDEDMAP)
    private Map<String,Object> exampleValue;

    @NotNull
    @ODBProperty(type = ODBProperty.PropertyType.EMBEDDEDMAP)
    private Map<String,Object> defaultValue;

    @NotNull
    @ODBProperty(type = ODBProperty.PropertyType.STRING)
    private String mode;

    @NotNull
    @ODBProperty(type = ODBProperty.PropertyType.STRING)
    private String workflow;

    /**
     * Default Constructor
     */
    public CustomProperty() {
        super();
        this.domainLocation = new HashMap<>();
        this.value = new HashMap<>();
        this.exampleValue = new HashMap<>();
        this.defaultValue = new HashMap<>();
    }

    /**
     * Constructor to Clone a Custom Object Object.
     * @param customProperty Object to Clone.
     */
    public CustomProperty(CustomProperty customProperty) {
        super(customProperty);
        this.ownerUUID = customProperty.getOwnerUUID();
        this.domainLocation = customProperty.getDomainLocation();
        this.domainLocationOrder = customProperty.getDomainLocationOrder();
        this.type = customProperty.getType();
        this.value = customProperty.getValue();
        this.exampleValue = customProperty.getExampleValue();
        this.defaultValue = customProperty.getDefaultValue();
        this.mode = customProperty.getMode();
        this.workflow = customProperty.getWorkflow();
    }

    public String getOwnerUUID() {
        return ownerUUID;
    }

    public void setOwnerUUID(String ownerUUID) {
        this.ownerUUID = ownerUUID;
    }

    public Map<String,Object> getDomainLocation() {
        return domainLocation;
    }

    public void setDomainLocation(Map<String,Object> domainLocation) {
        if (domainLocation == null) {
            this.domainLocation = new HashMap<>();
        } else {
            this.domainLocation = domainLocation;
        }
    }

    public int getDomainLocationOrder() {
        return domainLocationOrder;
    }

    public void setDomainLocationOrder(int domainLocationOrder) {
        this.domainLocationOrder = domainLocationOrder;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String,Object> getValue() {
        return value;
    }

    public void setValue(Map<String,Object> value) {
        if (value == null) {
           this.value = new HashMap<>();
        } else {
            this.value = value;
        }
    }

    public Map<String,Object> getExampleValue() {
        return exampleValue;
    }

    public void setExampleValue(Map<String,Object> exampleValue) {
        if (exampleValue == null) {
            this.exampleValue = new HashMap<>();
        } else {
            this.exampleValue = exampleValue;
        }
    }

    public Map<String,Object> getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(Map<String,Object> defaultValue) {
        if (defaultValue == null) {
            this.defaultValue = new HashMap<>();
        } else {
            this.defaultValue = defaultValue;
        }
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getWorkflow() {
        return workflow;
    }

    public void setWorkflow(String workflow) {
        this.workflow = workflow;
    }

    /**
     * Helper Method getDomainLocationName
     * @return String of Domain Location 'name' key found in DomainLocation Map.
     */
    @JsonIgnore
    public String getDomainLocationName() {
        return (String)this.domainLocation.get(CustomFieldConstants.MAP_KEY_NAME);
    }
    public void setDomainLocationName(String domainLocationName) {
        this.domainLocation.put(CustomFieldConstants.MAP_KEY_NAME,domainLocationName);
    }

    /**
     * Helper Method getDomainLocationUUID
     * @return String of Domain Location 'uuid' key found in DomainLocation Map.
     */
    @JsonIgnore
    public String getDomainLocationUUID() {
        return (String)this.domainLocation.get(CustomFieldConstants.MAP_KEY_UUID);
    }
    public void setDomainLocationUUID(String domainLocationUUID) {
        this.domainLocation.put(CustomFieldConstants.MAP_KEY_UUID,domainLocationUUID);
    }
    public void setDomainLocationKeyAndValue(String domainLocationKey, Object domainLocationValue) {
        this.domainLocation.put(domainLocationKey,domainLocationValue);
    }


}
