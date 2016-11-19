package jeffaschenk.orientdb.schema;

import com.orientechnologies.orient.core.metadata.schema.OType;
import jeffaschenk.orientdb.annotations.ODBAssociation;
import jeffaschenk.orientdb.annotations.ODBProperty;

/**
 * SchemaEntityPropertyTransformer
 *
 * @author jeffaschenk@gmail.com on 4/5/16.
 */
public class SchemaEntityPropertyTransformer {

    /**
     * Transform Entity Property enum Type to Persistent Store OType.
     *
     * @param propertyType Property enum
     * @return OType or null if not resolved.
     */
    protected static OType entityPropertyTypeToOType(ODBProperty.PropertyType propertyType) {
        OType oType = null;
        switch (propertyType) {
            case STRING:
                oType = OType.STRING;
                break;
            case BOOLEAN:
                oType = OType.BOOLEAN;
                break;
            case INTEGER:
                oType = OType.INTEGER;
                break;
            case SHORT:
                oType = OType.SHORT;
                break;
            case LONG:
                oType = OType.LONG;
                break;
            case FLOAT:
                oType = OType.FLOAT;
                break;
            case DOUBLE:
                oType = OType.DOUBLE;
                break;
            case DECIMAL:
                oType = OType.DECIMAL;
                break;
            case DATE:
                oType = OType.DATE;
                break;
            case DATETIME:
                oType = OType.DATETIME;
                break;
            case EMBEDDED:
                oType = OType.EMBEDDED;
                break;
            case EMBEDDEDLIST:
                oType = OType.EMBEDDEDLIST;
                break;
            case EMBEDDEDMAP:
                oType = OType.EMBEDDEDMAP;
                break;
            case EMBEDDEDSET:
                oType = OType.EMBEDDEDSET;
                break;
            case LINK:
                oType = OType.LINK;
                break;
            case LINKLIST:
                oType = OType.LINKLIST;
                break;
            case LINKSET:
                oType = OType.LINKSET;
                break;
            case LINKMAP:
                oType = OType.LINKMAP;
                break;
            case UUID:
                oType = OType.STRING;
                break;
            default:
                break;
        }
        return oType;
    }

    /**
     * Transform Entity Property enum Type to Persistent Store OType.
     *
     * @param associationType Property enum
     * @return OType or null if not resolved.
     */
    protected static OType entityAssociationTypeToOType(ODBAssociation.AssociationType associationType) {
        OType oType = null;
        switch (associationType) {
            case EMBEDDEDLIST:
                oType = OType.EMBEDDEDLIST;
                break;
            case EMBEDDEDMAP:
                oType = OType.EMBEDDEDMAP;
                break;
            case EMBEDDEDSET:
                oType = OType.EMBEDDEDSET;
                break;
            case LINK:
                oType = OType.LINK;
                break;
            case LINKLIST:
                oType = OType.LINKLIST;
                break;
            case LINKSET:
                oType = OType.LINKSET;
                break;
            case LINKMAP:
                oType = OType.LINKMAP;
                break;
            case UUID:
                oType = OType.STRING;
                break;
            default:
                break;
        }
        return oType;
    }

    /**
     * Transform Entity Property enum Type to Persistent Store OType.
     *
     * @param linkedType Property enum
     * @return OType or null if not resolved.
     */
    protected static OType entityLinkedTypeToOType(ODBAssociation.LinkedType linkedType) {
        OType oType = null;
        switch (linkedType) {
            case BINARY:
                oType = OType.BINARY;
                break;
            case BYTE:
                oType = OType.BYTE;
                break;
            case STRING:
                oType = OType.STRING;
                break;
            case BOOLEAN:
                oType = OType.BOOLEAN;
                break;
            case INTEGER:
                oType = OType.INTEGER;
                break;
            case SHORT:
                oType = OType.SHORT;
                break;
            case LONG:
                oType = OType.LONG;
                break;
            case FLOAT:
                oType = OType.FLOAT;
                break;
            case DOUBLE:
                oType = OType.DOUBLE;
                break;
            case DECIMAL:
                oType = OType.DECIMAL;
                break;
            case DATE:
                oType = OType.DATE;
                break;
            case DATETIME:
                oType = OType.DATETIME;
                break;
            case EMBEDDED:
                oType = OType.EMBEDDED;
                break;
            case EMBEDDEDLIST:
                oType = OType.EMBEDDEDLIST;
                break;
            case EMBEDDEDMAP:
                oType = OType.EMBEDDEDMAP;
                break;
            case EMBEDDEDSET:
                oType = OType.EMBEDDEDSET;
                break;
            case LINK:
                oType = OType.LINK;
                break;
            case LINKLIST:
                oType = OType.LINKLIST;
                break;
            case LINKSET:
                oType = OType.LINKSET;
                break;
            case LINKMAP:
                oType = OType.LINKMAP;
                break;
            default:
                break;
        }
        return oType;
    }

    /**
     * Transform Entity Property enum Type to Persistent Store OType.
     *
     * @param linkedType Property enum
     * @return OType or null if not resolved.
     */
    protected static OType entityLinkedTypeToOType(ODBProperty.LinkedType linkedType) {
        OType oType = null;
        switch (linkedType) {
            case BINARY:
                oType = OType.BINARY;
                break;
            case BYTE:
                oType = OType.BYTE;
                break;
            case STRING:
                oType = OType.STRING;
                break;
            case BOOLEAN:
                oType = OType.BOOLEAN;
                break;
            case INTEGER:
                oType = OType.INTEGER;
                break;
            case SHORT:
                oType = OType.SHORT;
                break;
            case LONG:
                oType = OType.LONG;
                break;
            case FLOAT:
                oType = OType.FLOAT;
                break;
            case DOUBLE:
                oType = OType.DOUBLE;
                break;
            case DECIMAL:
                oType = OType.DECIMAL;
                break;
            case DATE:
                oType = OType.DATE;
                break;
            case DATETIME:
                oType = OType.DATETIME;
                break;
            case EMBEDDED:
                oType = OType.EMBEDDED;
                break;
            case EMBEDDEDLIST:
                oType = OType.EMBEDDEDLIST;
                break;
            case EMBEDDEDMAP:
                oType = OType.EMBEDDEDMAP;
                break;
            case EMBEDDEDSET:
                oType = OType.EMBEDDEDSET;
                break;
            case LINK:
                oType = OType.LINK;
                break;
            case LINKLIST:
                oType = OType.LINKLIST;
                break;
            case LINKSET:
                oType = OType.LINKSET;
                break;
            case LINKMAP:
                oType = OType.LINKMAP;
                break;
            default:
                break;
        }
        return oType;
    }


}
