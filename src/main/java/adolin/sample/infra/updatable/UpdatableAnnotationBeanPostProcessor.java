package adolin.sample.infra.updatable;

import adolin.sample.infra.annotations.UpdatableBean;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.Ordered;

/**
 * Обработчик бинов, который позволяет регистрировать бины с обновляемыми свойствами.
 *
 * @author Adolin Negash 17.05.2021
 */
public class UpdatableAnnotationBeanPostProcessor implements BeanPostProcessor, BeanFactoryAware, Ordered {

    private final Map<String, Pair<Object, UpdatableBean>> beansMap = new HashMap<>();

    @Autowired
    private ConfigurableListableBeanFactory beanFactory;

    @Autowired
    private UpdatableBeanRegistry registry;

    /**
     * Обрабатывает бины до того, как они будут обернуты в proxy-сервера.
     *
     * @param bean     бин.
     * @param beanName имя бина.
     * @return исходный бин.
     * @throws BeansException ошибка при обработке.
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {

        UpdatableBean annotation = beanFactory.findAnnotationOnBean(beanName, UpdatableBean.class);
        if (annotation != null) {
            final BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);
            final String scope = beanDefinition.getScope();
            if (StringUtils.isNoneBlank(scope) && !BeanDefinition.SCOPE_SINGLETON.equals(scope)) {
                throw new IllegalStateException(
                    String.format("Cannot use scope %s with annotation UpdatableBean in bean [%s]", scope, beanName));
            }
            beansMap.put(beanName, Pair.of(bean, annotation));
        }
        return bean;
    }

    /**
     * Обрабатывает бины после того, как они обернуты в прокси.
     *
     * @param proxyBean бин.
     * @param beanName  имя бина.
     * @return исходный бин.
     * @throws BeansException ошибка при обработке.
     */
    @Override
    public Object postProcessAfterInitialization(Object proxyBean, String beanName) throws BeansException {

        Pair<Object, UpdatableBean> pair = beansMap.get(beanName);
        if (pair != null) {
            registry.registerBean(beanName, pair.getLeft(), proxyBean, pair.getRight());
            beansMap.remove(beanName);
        }
        return proxyBean;
    }

    /**
     * Порядок выполнения BeanPostProcessor-а.
     */
    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE;
    }

    /**
     * Сеттер фабрики бинов.
     * @param beanFactory фабрика бинов.
     */
    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        if (!(beanFactory instanceof ConfigurableListableBeanFactory)) {
            throw new IllegalArgumentException(
                "UpdatableAnnotationBeanPostProcessor requires a ConfigurableListableBeanFactory: " + beanFactory);
        }
        this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
    }
}
