package adolin.sample.infra.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.AliasFor;

/**
 * < ... description>
 *
 * @author Adolin Negash 13.05.2021
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Bean
public @interface Updatable {

  /**
   * @return
   */
  @AliasFor(value = "value", annotation = Bean.class)
  String[] value() default {};
}
