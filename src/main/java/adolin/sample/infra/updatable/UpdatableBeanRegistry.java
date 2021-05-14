package adolin.sample.infra.updatable;

import java.util.Collection;
import java.util.List;

/**
 * Реестр обновляемых свойств. Хранит свойства и обновляет их в привязанных бинах.
 *
 * @author Adolin Negash 13.05.2021
 */
public interface UpdatableBeanRegistry {

  /**
   * Добавляет в реестр бин с оновляемыми свойствами.
   *
   * @param bean бин
   * @return true - если бин содержит поле, которое будет обновляться (помечено аннотацей {@link adolin.sample.infra.annotations.UpdatableValue}).
   */
  boolean addBean(Object bean);

  /**
   * Возвращает список свойств.
   *
   * @return список свойств.
   */
  Collection<String> getProperties();

  /**
   * Обновляет заданные свойства.
   *
   * @param listOfValues список обновляемых свойств и их значений.
   */
  void updateProperties(List<PropertyValue> listOfValues);
}
