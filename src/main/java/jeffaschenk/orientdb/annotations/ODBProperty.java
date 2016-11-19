package jeffaschenk.orientdb.annotations;

import org.springframework.util.StringUtils;

import java.lang.annotation.*;

/**
 * ODBProperty
 *
 *
 * TODO:
 * + Support COLLATE :  CI: Case Insensitive or CS: Case Sensitive
 * + Support MIN
 * + Support MAX
 *
 * @author jeffaschenk@gmail.com on 3/31/16.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
@Documented
public @interface ODBProperty {
    /**
     * Name of the Property, if different from derived Name.
     * @return String representing Name of Field/Property.
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
     * String REGEX TO be Applied to the Property Attribute.
     * @return String containing the RegEx Pattern to be Applied.
     */
    String regex() default "";

    /**
     * Property Type specified for this Field or Method.
     * @return PropertyType
     */
    PropertyType type() default PropertyType.STRING;

    /**
     * Property Types
     */
    enum PropertyType {
        STRING,
        BOOLEAN,
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
        LINKMAP,
        TRANSIENT,
        UUID;

        /**
         * Get the Type by Name
         *
         * @param typeName to be used to lookup up by String Name of Type.
         * @return PropertyType Resolved or Null if unable to resolve.
         */
        public static PropertyType getTypeByName(String typeName) {
            if (StringUtils.isEmpty(typeName)) {
                return null;
            }
            for (PropertyType element : PropertyType.values()) {
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
