package adolin.sample.infra.updatable;

import adolin.sample.infra.annotations.UpdatableBean;
import java.util.Collection;
import java.util.List;

/**
 * Реестр обновляемых свойств. Хранит свойства и обновляет их в привязанных бинах.
 *
 * @author Adolin Negash 13.05.2021
 */
public interface UpdatableBeanRegistry {

    /**
     * Регистрирует в реестре бин с обновляемыми свойствами.
     *
     * @param beanName   имя бина.
     * @param bean       бин.
     * @param proxyBean  запроксированный бин.
     * @param annotation аннотация.
     */
    void registerBean(String beanName, Object bean, Object proxyBean, UpdatableBean annotation);

    /**
     * Возвращает список свойств.
     *
     * @return список свойств.
     */
    Collection<PropertyValue> getProperties();

    /**
     * Обновляет заданные свойства.
     *
     * @param listOfValues список обновляемых свойств и их значений.
     */
    void updateProperties(Collection<PropertyValue> listOfValues) throws Exception;
}
