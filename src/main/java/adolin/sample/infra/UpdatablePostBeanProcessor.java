package adolin.sample.infra;

import com.sun.el.util.ReflectionUtil;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

/**
 * < ... description>
 *
 * @author Adolin Negash 13.05.2021
 */
@Component
public class UpdatablePostBeanProcessor implements BeanPostProcessor, UpdatableBeanRegistry {

  private final Map<String, UpdatableProperty> properties = new HashMap<>();

  /**
   * @param bean
   * @param beanName
   * @return
   * @throws BeansException
   */
  @Override
  public Object postProcessBeforeInitialization(Object bean, String beanName)
      throws BeansException {

    // ReflectionUtils.
    // get class
    Class<?> beanClass = bean.getClass();

    // get fields

    return bean;
  }

  List<Object> getAnnotation(Class<?> beanClass, Class<?> annotation) {
    return null;
  }

  @Override
  public UpdatableProperty getProperty(String propertyName) {
    return properties.get(propertyName);
  }

  @Override
  public void updateProperties(List<PropertyValue> values) {
  }
}
