package adolin.sample.infra.updatable;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

/**
 * Информация об обновляемом поле.
 */
@Getter
class UpdatableProperty {

  /**
   * Список полей бинов, которые привязаны к свойству.
   */
  private final List<BeanFieldInfo> beans = new ArrayList<>();
}
