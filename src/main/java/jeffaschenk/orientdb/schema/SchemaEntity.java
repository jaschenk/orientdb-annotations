package jeffaschenk.orientdb.schema;

import jeffaschenk.orientdb.annotations.ODBGraphObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SchemaEntity
 * Represents a Schema Definition Object.
 *
 * @author jeffaschenk@gmail.com on 4/4/16.
 */
public class SchemaEntity {

    private Integer dependencyId;

    private Integer dependencyWeight;

    private String entityName;

    private Class entityClass;

    private ODBGraphObject.ODBGraphObjectType graphType;

    private String overrideEntityName;

    private boolean rootClass;

    private boolean abstractClass;

    private boolean mappedEntity;

    private boolean mappedEntityValidated;

    private final List<Class> inheritanceChain = new ArrayList<>();

    private final Map<String, SchemaEntityProperty> properties = new HashMap<>();

    private final Map<String, Object> entityData = new HashMap<>();

    /**
     * Defailt Constructor to Instantiate a Schema Definition
     * @param dependencyId Dependency Id.
     * @param entityName Name of the Entity Object.
     * @param entityClass Class of the Entity Object.
     */
    public SchemaEntity(Integer dependencyId, String entityName, Class entityClass) {
        this.dependencyId = dependencyId;
        this.dependencyWeight = 0;
        this.entityName = entityName;
        this.entityClass = entityClass;
        this.mappedEntity = false;
        this.mappedEntityValidated = false;
        this.abstractClass = false;
    }

    public Integer getDependencyId() {
        return dependencyId;
    }

    public Integer getDependencyWeight() {
        return dependencyWeight;
    }

    public void setDependencyWeight(Integer dependencyWeight) {
        this.dependencyWeight = dependencyWeight;
    }

    public String getEntityName() {
        return entityName;
    }

    public Class getEntityClass() {
        return entityClass;
    }

    public Map<String, Object> getEntityData() {
        return entityData;
    }

    public List<Class> getInheritanceChain() {
        return inheritanceChain;
    }

    public Map<String, SchemaEntityProperty> getProperties() {
        return properties;
    }

    public boolean isMappedEntityValidated() {
        return mappedEntityValidated;
    }

    public boolean isMappedEntity() {
        return mappedEntity;
    }

    public void setMappedEntity(boolean mappedEntity) {
        this.mappedEntity = mappedEntity;
    }

    public void setMappedEntityValidated(boolean mappedEntityValidated) {
        this.mappedEntityValidated = mappedEntityValidated;
    }

    public boolean isAbstractClass() {
        return abstractClass;
    }

    public void setAbstractClass(boolean abstractClass) {
        this.abstractClass = abstractClass;
    }

    public ODBGraphObject.ODBGraphObjectType getGraphType() {
        return graphType;
    }

    public void setGraphType(ODBGraphObject.ODBGraphObjectType graphType) {
        this.graphType = graphType;
    }

    public String getOverrideEntityName() {
        return overrideEntityName;
    }

    public void setOverrideEntityName(String overrideEntityName) {
        this.overrideEntityName = overrideEntityName;
    }

    public boolean isRootClass() {
        return rootClass;
    }

    public void setRootClass(boolean rootClass) {
        this.rootClass = rootClass;
    }

    public String resolveName() {
        if (this.overrideEntityName == null || this.overrideEntityName.isEmpty()) {
            return this.entityClass.getSimpleName();
        } else {
            return this.overrideEntityName;
        }
    }

    @Override
    public String toString() {
        return "SchemaEntity{" +
                "Name='" + entityName + '\'' +
                ", OverrideName='"+ overrideEntityName + '\'' +
                ", Class='" + entityClass + '\'' +
                ", GraphType='" + graphType + '\'' +
                ", Abstract='" + abstractClass + '\'' +
                ", RootClass='" + rootClass + '\'' +
                ", mappedEntity='" + mappedEntity + '\'' +
                ", mappedEntityValidated='" + mappedEntityValidated + '\'' +
                ", NumberOfProperties='" + properties.size() + '\'' +
                '}';
    }
}
