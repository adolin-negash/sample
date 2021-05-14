package adolin.sample.infra.updatable;

import adolin.sample.infra.annotations.UpdatableBean;
import java.util.Map;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Ищет бины, у которых могут обновляться поля при изменении свойств.
 *
 * @author Adolin Negash 13.05.2021
 */
public class UpdatableFieldsInitializer implements ApplicationContextAware {

  @Autowired
  private UpdatableBeanRegistry beanRegistry;

  private void setup(ApplicationContext applicationContext) {
    Map<String, Object> beans = applicationContext.getBeansWithAnnotation(UpdatableBean.class);
    for (Map.Entry<String, Object> es : beans.entrySet()) {
      UpdatableBean annotation = applicationContext.findAnnotationOnBean(es.getKey(), UpdatableBean.class);
      beanRegistry.addBean(es.getValue(), es.getKey(), annotation);
    }
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    setup(applicationContext);
  }
}
