package adolin.sample.infra.updatable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Информация о сеттере бина.
 *
 * @author Adolin Negash 17.05.2021
 * @see adolin.sample.infra.annotations.UpdatableBean
 */
@Slf4j
@Getter
public class BeanMethodInfo extends BeanMemberInfo {

    /**
     * Метод сеттера.
     */
    private final Method setter;

    /**
     * Создает объект с информацией о методах-сеттерах бина, используемых для обновляемых полей.
     *
     * @param bean     бин
     * @param beanName имя бина.
     * @param setter   метод-сеттер.
     */
    @SuppressWarnings("java:S3011")
    BeanMethodInfo(Object bean, String beanName, Method setter) {
        super(bean, beanName);
        this.setter = Objects.requireNonNull(setter);
        this.setter.setAccessible(true);
    }

    /**
     * Передает новое значение в сеттер.
     *
     * @param value новое значение.
     */
    @Override
    protected void setValue(String value) {
        try {
            if (log.isDebugEnabled()) {
                log.debug("Change value of type [{}], setter [{}]", bean.getClass(), setter.getName());
            }

            setter.invoke(bean, value);
        } catch (IllegalAccessException e) {
            log.error("Cannot update property - no access to setter {}.{}.", bean.getClass(), setter.getName(), e);
        } catch (InvocationTargetException e) {
            log.error("Cannot update property, internal error in setter {}.{}.", bean.getClass(), setter.getName(), e);
        }
    }
}
