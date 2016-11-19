package jeffaschenk.orientdb.annotations;

import java.lang.annotation.*;

/**
 * ODBGraphObject
 *
 * Created by danacraigmaher on 3/24/16.
 * @author jeffaschenk@gmail.com on 3/31/16.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
@Documented
public @interface ODBGraphObject {
    /**
     * Overrides the Name of the Graph Object.
     *
     * @return String representing Name of Graph Object, can be Null, in which case the Name of the
     * Type Class itself is used..
     */
    String name() default "";

    /**
     * Indicates that this Class is a Base Root Class and that during Schema Validation we
     * ensure our SuperClass is 'V'.
     *
     * @return boolean indicator if this Class is a Root Class or Not.
     */
    boolean rootClass() default false;

    /**
     * ODB Graph Object Type
     *
     * @return ODB_GRAPH_OBJECT_TYPE to be specified for this Object.
     */
    ODBGraphObjectType type() default ODBGraphObjectType.VERTEX;

    /**
     * ODB Graph Object Types
     */
    enum ODBGraphObjectType { VERTEX, EDGE, NONE }
}
