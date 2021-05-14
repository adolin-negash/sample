package adolin.sample.infra.updatable;

import java.lang.reflect.Field;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Информация о поле бина.
 */
@Getter
@RequiredArgsConstructor
class BeanFieldInfo {

  /**
   * Бин.
   */
  private final Object bean;

  /**
   * Поле в бине.
   */
  private final Field field;
}
