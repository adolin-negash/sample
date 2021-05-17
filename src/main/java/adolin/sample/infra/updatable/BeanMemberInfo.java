package adolin.sample.infra.updatable;

import lombok.Getter;
import lombok.Setter;

/**
 * < ... description>
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
     *
     */
    @Setter
    protected String beanName;

    /**
     *
     */
    protected final String property;

    /**
     * @param bean
     * @param property
     */
    protected BeanMemberInfo(Object bean, String property) {
        this.bean = bean;
        this.property = property;
    }

    /**
     * @param value
     */
    protected abstract void setValue(String value);
}
