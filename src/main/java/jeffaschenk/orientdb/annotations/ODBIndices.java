package jeffaschenk.orientdb.annotations;

import java.lang.annotation.*;

/**
 * ODBIndices
 *
 * @author jeffaschenk@gmail.com on 5/18/16.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
@Documented
public @interface ODBIndices {
        ODBIndex[] value();
}
