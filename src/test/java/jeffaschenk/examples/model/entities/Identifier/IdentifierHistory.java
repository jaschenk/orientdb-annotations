package jeffaschenk.examples.model.entities.Identifier;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jeffaschenk.orientdb.annotations.ODBGraphObject;
import jeffaschenk.orientdb.annotations.ODBIndex;
import jeffaschenk.orientdb.annotations.ODBProperty;
import jeffaschenk.examples.model.serialization.JsonDateSerializer;

import java.io.Serializable;
import java.time.Instant;
import java.util.Date;

/**
 * IdentifierHistory
 *
 * Provides away for each Services Environment Instance can
 * assure that a generated ID is unique with that environment.
 *
 */
@ODBGraphObject(type = ODBGraphObject.ODBGraphObjectType.VERTEX, rootClass = true)
@ODBIndex(name = "IdentifierHistory.id",
        engineType = ODBIndex.EngineType.SBTREE,
        sql = "CREATE INDEX IdentifierHistory.hid ON IdentifierHistory (id) UNIQUE")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IdentifierHistory implements Serializable {
    /**
     * Identifier.
     */
    @ODBProperty(name = "id", mandatory = true, notNull = true)
    private String id;
    /**
     * Associated Type.
     */
    @ODBProperty(type = ODBProperty.PropertyType.STRING, mandatory = true, notNull = true)
    private String associatedType;
    /**
     * Associated UUID.
     */
    @ODBProperty(type = ODBProperty.PropertyType.STRING, mandatory = true, notNull = true)
    private String associatedOwnerUUID;
    /**
     * Entity Created By Email Address of User Creating this Entity,
     * should be maintain by applicable service layer.
     */
    @ODBProperty(type = ODBProperty.PropertyType.STRING, mandatory = true, notNull = true)
    private String createdBy;
    /**
     * Entity Created By Date, Date in which this entity was created,
     * should be maintain by applicable service layer.
     */
    @JsonSerialize(using=JsonDateSerializer.class)
    @ODBProperty(type = ODBProperty.PropertyType.DATETIME, mandatory = true, notNull = true)
    private Date createdByDate;

    /**
     * Default Constructor
     */
    public IdentifierHistory() {
    }

    /**
     * Constructor to allow easy instantiation for Services Layer.
     *
     * @param associatedType Associated Class Name.
     * @param associatedOwnerUUID Associated Class UUID.
     * @param createdBy Created By Email Address or "SYSTEM".
     */
    public IdentifierHistory(String associatedType, String associatedOwnerUUID, String createdBy) {
        this.associatedType = associatedType;
        this.associatedOwnerUUID = associatedOwnerUUID;
        this.createdBy = createdBy;
        this.createdByDate = Date.from(Instant.now());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAssociatedType() {
        return associatedType;
    }

    public void setAssociatedType(String associatedType) {
        this.associatedType = associatedType;
    }

    public String getAssociatedOwnerUUID() {
        return associatedOwnerUUID;
    }

    public void setAssociatedOwnerUUID(String associatedOwnerUUID) {
        this.associatedOwnerUUID = associatedOwnerUUID;
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

    @Override
    public String toString() {
        return "IdentifierHistory{" +
                "Id='" + id + '\'' +
                ", associatedType='" + associatedType + '\'' +
                ", associatedOwnerUUID='" + associatedOwnerUUID + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", createdByDate=" + createdByDate +
                '}';
    }
}
