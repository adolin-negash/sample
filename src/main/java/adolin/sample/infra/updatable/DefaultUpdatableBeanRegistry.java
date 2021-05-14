package adolin.sample.infra.updatable;

import adolin.sample.infra.annotations.UpdatableBean;
import adolin.sample.infra.annotations.UpdatableValue;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
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
   * @param bean       бин.
   * @param beanName   имя бина.
   * @param annotation аннотация.
   */
  @Override
  public void addBean(Object bean, String beanName, UpdatableBean annotation) {

    Objects.requireNonNull(bean, "empty bean");

    Class<?> beanClass = bean.getClass();

    final List<Pair<Field, UpdatableValue>> fields = getProperties(beanClass);
    for (Pair<Field, UpdatableValue> pair : fields) {
      final String propertyName = pair.getRight().value();
      final Field field = pair.getLeft();
      final UpdatableProperty property = properties.computeIfAbsent(propertyName, n -> new UpdatableProperty());

      property.getBeans().add(new BeanFieldInfo(bean, field));

      setBeanField(bean, field, environment.getProperty(propertyName));
    }

    final List<Method> setters = getSetters(beanClass);
    for (Method method : setters) {

    }
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
      setBeanField(beanInfo.getBean(), beanInfo.getField(), value);
    }
  }

  private void setBeanField(Object bean, Field field, String value) {
    try {
      if (log.isDebugEnabled()) {
        log.debug("Change value of type {}, field {}",
            bean.getClass(), field.getName());
      }

      FieldUtils.writeField(field, bean, value, true);
    } catch (IllegalAccessException e) {
      log.error("Cannot update property no access to field {}.{}.\nError: {}",
          bean.getClass(), field.getName(), e.getMessage());
    }
  }

  private List<Pair<Field, UpdatableValue>> getProperties(Class<?> beanClass) {
    return FieldUtils.getAllFieldsList(beanClass).stream()
        .filter(this::isFieldValid)
        .map(field -> Pair.of(field, field.getAnnotation(UpdatableValue.class)))
        .filter(pair -> pair.getRight() != null)
        .collect(Collectors.toList());
  }

  private List<Method> getSetters(Class<?> beanClass) {
    return MethodUtils.getMethodsListWithAnnotation(beanClass, UpdatableValue.class, true, true)
        .stream()
        .filter(this::isValidSetter)
        .collect(Collectors.toList());
  }

  private boolean isFieldValid(Field field) {
    final Class<?> type = field.getType();
    final int modifiers = field.getModifiers();

    return type.isAssignableFrom(String.class)
        && !Modifier.isFinal(modifiers)
        && !Modifier.isStatic(modifiers);
  }

  private boolean isValidSetter(Method method) {
    Class<?>[] params = method.getParameterTypes();
    if (params.length != 1) {
      return false;
    }
    return params[0].isAssignableFrom(String.class);
  }
}
