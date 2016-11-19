package jeffaschenk.orientdb.annotations;

import java.lang.annotation.*;

/**
 * ODBIndex
 *
 * @author jeffaschenk@gmail.com on 3/31/16.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
@Documented
@Repeatable(ODBIndices.class)
public @interface ODBIndex {

    /**
     * Type of the Index Engine.
     * @return EngineType representing Type of Index Engine.
     */
    EngineType engineType() default EngineType.SBTREE;

    /**
     * Index Engine Types
     */
    enum EngineType {
        SBTREE,
        HASHINDEX,
        LUCENE
    }

    /**
     * Type of the Index Engine.
     * @return EngineType representing Type of Index Engine.
     */
    IndexType type() default IndexType.UNIQUE;

    /**
     * Index Engine Types
     */
    enum IndexType {
        UNIQUE,
        NOTEUNIQUE,
        FULLTEXT,
        DICTIONARY,
        SPATIAL
    }

    /**
     * Name of the Index.
     * @return String representing Name of Index.
     */
    String name() default "";

    /**
     * Values of Property Names which make up the Index.
     * @return String[] Array of Property Name Making up the Index.
     */
    String[] properties() default {""};

    /**
     * Actual specified SQL of the Index to be Defined.
     * @return String representing Name of Index.
     */
    String sql() default "";

}
