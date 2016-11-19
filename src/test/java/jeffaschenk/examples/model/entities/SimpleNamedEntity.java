package jeffaschenk.examples.model.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jeffaschenk.orientdb.annotations.ODBGraphObject;
import jeffaschenk.orientdb.annotations.ODBIndex;
import jeffaschenk.orientdb.annotations.ODBProperty;
import jeffaschenk.examples.model.serialization.JsonDateSerializer;

import java.io.Serializable;
import java.util.Date;

/**
 * SimpleNamedEntity
 */
@ODBGraphObject(type = ODBGraphObject.ODBGraphObjectType.VERTEX, rootClass = true)
@ODBIndex(name = "SimpleNamedEntity.name",
        engineType = ODBIndex.EngineType.SBTREE,
        sql = "CREATE INDEX SimpleNamedEntity.name ON SimpleNamedEntity (name COLLATE ci) UNIQUE")
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class SimpleNamedEntity implements Serializable {
    /**
     * Unique Entity Name.
     */
    @ODBProperty(type = ODBProperty.PropertyType.STRING, mandatory = true, notNull = true)
    private String name;
    /**
     * Entity Description.
     */
    @ODBProperty(type = ODBProperty.PropertyType.STRING)
    private String description;
    /**
     * Entity Status
     */
    @ODBProperty(type = ODBProperty.PropertyType.STRING)
    private String status;
    /**
     * Entity Created By Date, Date in which this entity was created,
     * should be maintain by applicable service layer.
     */
    @JsonSerialize(using=JsonDateSerializer.class)
    @ODBProperty(type = ODBProperty.PropertyType.DATETIME)
    private Date createdByDate;
    /**
     * Entity Updated By Date, Date in which this entity was updated,
     * should be maintain by applicable service layer.
     */
    @JsonSerialize(using=JsonDateSerializer.class)
    @ODBProperty(type = ODBProperty.PropertyType.DATETIME)
    private Date updatedByDate;

    /**
     * Default Constructor
     */
    public SimpleNamedEntity() {
        this.status = RootEntity.EntityStatus.ACTIVE.name();
    }

    /**
     * Constructor to Clone Entity.
     * @param entity to be Cloned.
     */
    public SimpleNamedEntity(SimpleNamedEntity entity) {
        this.name = entity.getName();
        this.description = entity.getDescription();
        this.status = entity.getStatus();
        this.createdByDate = entity.getCreatedByDate();
        this.updatedByDate = entity.getUpdatedByDate();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreatedByDate() {
        return createdByDate;
    }

    public void setCreatedByDate(Date createdByDate) {
        this.createdByDate = createdByDate;
    }

    public Date getUpdatedByDate() {
        return updatedByDate;
    }

    public void setUpdatedByDate(Date updatedByDate) {
        this.updatedByDate = updatedByDate;
    }

    @Override
    public String toString() {
        return "SimpleNamedEntity{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", createdByDate=" + createdByDate +
                ", updatedByDate=" + updatedByDate +
                '}';
    }
}
