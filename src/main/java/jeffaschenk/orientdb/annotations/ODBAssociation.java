package jeffaschenk.orientdb.annotations;

import org.springframework.util.StringUtils;

import java.lang.annotation.*;

/**
 * ODBAssociation
 *
 * @author jeffaschenk@gmail.com on 3/31/16.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
@Documented
public @interface ODBAssociation {

    /**
     * Name of the Association.
     * @return String representing Name of Association.
     */
    String name() default "";

    /**
     * Mandatory Property Indicator
     * @return boolean
     */
    boolean mandatory() default false;

    /**
     * Not Null Property Indicator
     * @return boolean
     */
    boolean notNull() default false;

    /**
     * Type oF Association
     *
     * @return AssociationType
     */
    AssociationType type() default AssociationType.NONE;

    /**
     * Name of Edge to be Defined if not specified.
     * @return String of Edge Name.
     */
    String edgeName() default "";

    /**
     * Type of Linked Type of Association if of Type Link...
     * @return String Name of Linked Type.
     */
    LinkedType linkedType() default LinkedType.NONE;

    /**
     * Type of Linked Type of Association if of Type Link...
     * @return String Name of Linked Type.
     */
    String linkedClassName() default "";

    /**
     * Association Types
     */
    enum AssociationType {
        NONE,
        EDGE,
        EMBEDDEDLIST,
        EMBEDDEDMAP,
        EMBEDDEDSET,
        LINK,
        LINKLIST,
        LINKSET,
        LINKMAP,
        TRANSIENT,
        UUID;

        /**
         * Get the Type by Name
         *
         * @param typeName to be used to lookup up by String Name of Type.
         * @return PropertyType Resolved or Null if unable to resolve.
         */
        public static AssociationType getTypeByName(String typeName) {
            if (StringUtils.isEmpty(typeName)) {
                return null;
            }
            for (AssociationType element : AssociationType.values()) {
                if (element.toString().equalsIgnoreCase(typeName) ||
                        element.toString().equalsIgnoreCase(typeName.replace(' ','_'))) {
                    return element;
                }
            }
            return null;
        }

    }

    /**
     * Linked Types
     */
    enum LinkedType {
        NONE,
        STRING,
        BINARY,
        BOOLEAN,
        BYTE,
        INTEGER,
        SHORT,
        LONG,
        FLOAT,
        DOUBLE,
        DECIMAL,
        DATE,
        DATETIME,
        EMBEDDED,
        EMBEDDEDLIST,
        EMBEDDEDMAP,
        EMBEDDEDSET,
        LINK,
        LINKLIST,
        LINKSET,
        LINKMAP

    }
}
