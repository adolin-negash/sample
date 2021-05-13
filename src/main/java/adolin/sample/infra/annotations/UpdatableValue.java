package adolin.sample.infra.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * < ... description>
 *
 * @author Adolin Negash 13.05.2021
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface UpdatableValue {

  /**
   * <Documents>
   *
   * @return
   */
  String value();
}
