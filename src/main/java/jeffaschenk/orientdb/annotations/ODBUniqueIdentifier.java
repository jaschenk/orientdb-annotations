package jeffaschenk.orientdb.annotations;

import java.lang.annotation.*;

/**
 * ODBUniqueIdentifier
 *
 * @author jeffaschenk@gmail.com on 3/31/16.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
@Documented
public @interface ODBUniqueIdentifier {
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
}
