package jeffaschenk.examples.model.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jeffaschenk.examples.model.serialization.JsonDateSerializer;
import jeffaschenk.orientdb.annotations.ODBGraphObject;
import jeffaschenk.orientdb.annotations.ODBIndex;
import jeffaschenk.orientdb.annotations.ODBProperty;
import jeffaschenk.orientdb.annotations.ODBUniqueIdentifier;

import java.io.Serializable;
import java.util.Date;

/**
 * RootEntity
 */
@ODBGraphObject(type = ODBGraphObject.ODBGraphObjectType.VERTEX, rootClass = true)
@ODBIndex(name = "RootEntity.uuid",
        engineType = ODBIndex.EngineType.SBTREE,
        sql = "CREATE INDEX RootEntity.uuid ON RootEntity (uuid) UNIQUE")
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class RootEntity implements Serializable {
    /**
     * Entity Identity UUID.
     */
    @ODBUniqueIdentifier(name = "uuid", mandatory = true, notNull = true)
    private String uuid;
    /**
     * Entity Name.
     */
    @ODBProperty(type = ODBProperty.PropertyType.STRING)
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
     * Entity Created By Email Address of a User Creating this Entity,
     * should be maintain by applicable service layer.
     */
    @ODBProperty(type = ODBProperty.PropertyType.STRING)
    private String createdBy;
    /**
     * Entity Created By Date, Date in which this entity was created,
     * should be maintain by applicable service layer.
     */
    @JsonSerialize(using=JsonDateSerializer.class)
    @ODBProperty(type = ODBProperty.PropertyType.DATETIME)
    private Date createdByDate;
    /**
     * Entity Updated By Email Address of User Updating this Entity,
     * should be maintain by applicable service layer.
     */
    @ODBProperty(type = ODBProperty.PropertyType.STRING)
    private String updatedBy;
    /**
     * Entity Updated By Date, Date in which this entity was updated,
     * should be maintain by applicable service layer.
     */
    @JsonSerialize(using=JsonDateSerializer.class)
    @ODBProperty(type = ODBProperty.PropertyType.DATETIME)
    private Date updatedByDate;
    /**
     * Request Information which was used to perform Update.
     */
    @ODBProperty(type = ODBProperty.PropertyType.STRING)
    private String updatedByRequest;
    /**
     * Resource Link
     */
    @ODBProperty(type = ODBProperty.PropertyType.TRANSIENT)
    private String resourceLink;

    /**
     * Default Constructor
     */
    public RootEntity() {
        this.status = EntityStatus.ACTIVE.name();
    }

    /**
     * Simple Entity Status Type
     */
    public enum EntityStatus {
        ACTIVE,
        INACTIVE,
        PENDING,
        DELETED
    }

    /**
     * Constructor to Clone Entity.
     * @param rootEntity Entity to be Cloned.
     */
    public RootEntity(RootEntity rootEntity) {
        this.resourceLink = rootEntity.getResourceLink();
        this.uuid = rootEntity.getUuid();
        this.name = rootEntity.getName();
        this.description = rootEntity.getDescription();
        this.status = rootEntity.getStatus();
        this.createdBy = rootEntity.getCreatedBy();
        this.createdByDate = rootEntity.getCreatedByDate();
        this.updatedBy = rootEntity.getUpdatedBy();
        this.updatedByDate = rootEntity.getUpdatedByDate();
        this.updatedByRequest = rootEntity.getUpdatedByRequest();
    }

    public String getResourceLink() {
        return resourceLink;
    }

    public void setResourceLink(String resourceLink) {
        this.resourceLink = resourceLink;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedByDate() {
        return createdByDate;
    }

    public void setCreatedByDate(Date createdByDate) {
        this.createdByDate = createdByDate;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Date getUpdatedByDate() {
        return updatedByDate;
    }

    public void setUpdatedByDate(Date updatedByDate) {
        this.updatedByDate = updatedByDate;
    }

    public String getUpdatedByRequest() {
        return updatedByRequest;
    }

    public void setUpdatedByRequest(String updatedByRequest) {
        this.updatedByRequest = updatedByRequest;
    }


    @Override
    public String toString() {
        return "RootEntity{" +
                "uuid='" + uuid + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", createdBy='" + createdBy + '\'' +
                ", createdByDate=" + createdByDate +
                ", updatedBy='" + updatedBy + '\'' +
                ", updatedByDate=" + updatedByDate +
                ", updatedByRequest='" + updatedByRequest + '\'' +
                '}';
    }
}
