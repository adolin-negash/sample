package adolin.sample.infra.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.AliasFor;

/**
 * Аннотация помечает бины, которые содержат обновляемые свойства.
 * @see UpdatableValue
 *
 * @author Adolin Negash 13.05.2021
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Inherited
@Bean
public @interface UpdatableBean {

  /**
   * Список названий бина.
   *
   * @return список
   */
  @AliasFor(value = "value", annotation = Bean.class)
  String[] value() default {};
}
