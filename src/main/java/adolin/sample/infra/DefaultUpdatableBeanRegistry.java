package adolin.sample.infra;

import adolin.sample.infra.annotations.UpdatableValue;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

/**
 * Реестр обновляемых свойств. Хранит свойства и обновляет их в привязанных бинах.
 *
 * @author Adolin Negash 14.05.2021
 */
@Slf4j
public class DefaultUpdatableBeanRegistry implements UpdatableBeanRegistry {

  private final HashMap<String, UpdatableProperty> properties = new HashMap<>();

  @Autowired
  private Environment environment;

  /**
   * Добавляет в реестр автообновляемый бин.
   *
   * @param bean бин
   * @return true - если бин содержит обновляемое свойство и может обновляться.
   */
  @Override
  public boolean addBean(Object bean) {
    Objects.requireNonNull(bean, "empty bean");
    final List<Pair<Field, UpdatableValue>> fields = getProperties(bean.getClass());
    if (fields.isEmpty()) {
      return false;
    }

    for (Pair<Field, UpdatableValue> pair : fields) {
      final String propertyName = pair.getRight().value();
      final UpdatableProperty updatableProperty = properties
          .computeIfAbsent(propertyName, UpdatableProperty::new);

      updatableProperty.getBeans().add(new BeanFieldInfo(bean, pair.getLeft()));
    }

    return true;
  }

  /**
   * Возвращает список свойств.
   *
   * @return список свойств.
   */
  @Override
  public Collection<String> getProperties() {
    return properties.keySet();
  }

  /**
   * Обновляет заданные свойства.
   *
   * @param listOfValues список обновляемых свойств и их значений.
   */
  @Override
  public void updateProperties(List<PropertyValue> listOfValues) {
    Objects.requireNonNull(listOfValues, "empty list");
    for (PropertyValue value : listOfValues) {
      updateProperty(value.getName(), value.getValue());
    }
  }

  private void updateProperty(String propertyName, String value) {
    final UpdatableProperty property = properties.get(propertyName);
    if (property == null) {
      log.warn("Cannot update property {}, no beans use this property.", propertyName);
      return;
    }

    for (BeanFieldInfo beanInfo : property.getBeans()) {
      final Object bean = beanInfo.getBean();
      final Field field = beanInfo.getField();
      try {

        if (log.isDebugEnabled()) {
          log.debug("Change value of type {}, field {} by property {}",
              bean.getClass(), field.getName(), propertyName);
        }

        FieldUtils.writeField(field, bean, value, true);
      } catch (IllegalAccessException e) {
        log.error("Cannot update property {}, no access to field {}.\nError: {}",
            propertyName, field.getName(), e.getMessage());
      }
    }
  }

  private List<Pair<Field, UpdatableValue>> getProperties(Class<?> beanClass) {
    return FieldUtils.getAllFieldsList(beanClass).stream()
        .filter(this::isFieldValid)
        .map(field -> Pair.of(field, field.getAnnotation(UpdatableValue.class)))
        .filter(pair -> pair.getRight() != null)
        .collect(Collectors.toList());
  }

  private boolean isFieldValid(Field field) {
    final Class<?> type = field.getType();
    final int modifiers = field.getModifiers();

    return type.isAssignableFrom(String.class)
        && !Modifier.isFinal(modifiers)
        && !Modifier.isStatic(modifiers);
  }
}
