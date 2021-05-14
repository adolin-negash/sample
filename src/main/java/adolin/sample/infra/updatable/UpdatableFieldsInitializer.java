package adolin.sample.infra.updatable;

import adolin.sample.infra.annotations.UpdatableBean;
import java.util.Map;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * 1. fetch beans and fill registry
 * <p>
 * 2. listen refresh and update
 *
 * @author Adolin Negash 13.05.2021
 */
public class UpdatableFieldsInitializer implements ApplicationContextAware {

  @Autowired
  private UpdatableBeanRegistry beanRegistry;

  private void setup(ApplicationContext applicationContext) {
    Map<String, Object> beans = applicationContext.getBeansWithAnnotation(UpdatableBean.class);
    for (Object bean : beans.values()) {
      beanRegistry.addBean(bean);
    }
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    setup(applicationContext);
  }
}
