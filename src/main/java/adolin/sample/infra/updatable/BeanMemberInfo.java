package adolin.sample.infra.updatable;

import lombok.Getter;

/**
 * Базовый класс информации о бине.
 *
 * @author Adolin Negash 17.05.2021
 */
@Getter
public abstract class BeanMemberInfo {

    /**
     * Бин.
     */
    protected final Object bean;

    /**
     * Имя бина.
     */
    private final String beanName;

    /**
     * @param bean     бин.
     * @param beanName имя бина.
     */
    protected BeanMemberInfo(Object bean, String beanName) {
        this.bean = bean;
        this.beanName = beanName;
    }

    /**
     * Изменяет значение свойства.
     *
     * @param value значение.
     */
    protected abstract void setValue(String value);
}
