package adolin.sample.infra.updatable;

import java.lang.reflect.Field;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.FieldUtils;

/**
 * Информация о поле бина.
 *
 * @author Adolin Negash 13.05.2021
 */
@Slf4j
@Getter
class BeanFieldInfo extends BeanMemberInfo {

    /**
     * Поле в бине.
     */
    private final Field field;

    /**
     * Создает объект с информацией о поле бина.
     *
     * @param bean     бин.
     * @param beanName название бина.
     * @param field    поле.
     */
    BeanFieldInfo(Object bean, String beanName, Field field) {
        super(bean, beanName);
        this.field = field;
    }

    /**
     * Изменяет поле бина.
     *
     * @param value новое значение.
     */
    @Override
    protected void setValue(String value) {
        try {
            if (log.isDebugEnabled()) {
                log.debug("Change value of type {}, field {}", bean.getClass(), field.getName());
            }

            FieldUtils.writeField(field, bean, value, true);
        } catch (IllegalAccessException e) {
            log.error("Cannot update property no access to field {}.{}.", bean.getClass(), field.getName(), e);
        }
    }
}
