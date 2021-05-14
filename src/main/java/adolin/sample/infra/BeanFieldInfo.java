package adolin.sample.infra;

import java.lang.reflect.Field;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *
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
