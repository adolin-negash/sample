package adolin.sample.infra.updatable;

import adolin.sample.infra.annotations.UpdatableBean;
import adolin.sample.infra.annotations.UpdatableValue;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
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

    private static final Class<?>[] PARAMETER_TYPES = {String.class};

    @Getter
    static class BeanInfo {

        private final Object bean;

        private final Method afterUpdateMethod;

        BeanInfo(Object bean, Method afterUpdateMethod) {
            this.bean = bean;
            this.afterUpdateMethod = afterUpdateMethod;
        }
    }

    private final HashMap<String, List<BeanMemberInfo>> properties = new HashMap<>();

    private final HashMap<String, BeanInfo> beans = new HashMap<>();

    @Autowired
    private Environment environment;

    /**
     * Регистрирует в реестре в реестр бин с оновляемыми свойствами.
     *
     * @param beanName   имя бина.
     * @param bean       бин.
     * @param proxyBean  запроксированный бин.
     * @param annotation аннотация.
     */
    @Override
    public void registerBean(String beanName, Object bean, Object proxyBean, UpdatableBean annotation) {
        Objects.requireNonNull(beanName, "empty beanName");
        Objects.requireNonNull(bean, "empty bean");
        Objects.requireNonNull(proxyBean, "empty proxyBean");
        Objects.requireNonNull(annotation, "empty bean annotation");

//        for (BeanMemberInfo info : updatableMembers) {
//            info.setBeanName(beanName);
//            final String propertyName = info.getProperty();
//            final List<BeanMemberInfo> membersList = properties.computeIfAbsent(propertyName, n -> new ArrayList<>());
//            membersList.add(info);
//        }

//            Class<?> originalClass = originalBean.getClass();
//            var membersInfo = new BeanMembersInfo()
//                .setBean(proxyBean)
//                .setFields(extractUpdatableFields(originalClass))
//                .setSetters(extractUpdatableSetters(originalClass))
//                .setAnnotation(null);
//
//            Class<?> proxyClass = proxyBean.getClass();
//
//            Stream<BeanMemberInfo> fieldMembers = membersInfo.getFields().stream()
//                .map(pair -> new BeanFieldInfo(originalBean, pair.getRight().value(), pair.getLeft()));
//
//            Stream<BeanMemberInfo> methodMembers = membersInfo.getSetters().stream()
//                .map(pair -> getBeanMemberInfo(proxyBean, originalBean, proxyClass, pair));
//
//            List<BeanMemberInfo> members = Stream.concat(fieldMembers, methodMembers)
//                .collect(Collectors.toList());
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

    private BeanMemberInfo getBeanMemberInfo(Object bean,
        Object originalBean,
        Class<?> proxyClass,
        Pair<Method, UpdatableValue> pair) {

        String property = pair.getRight().value();
        Method setter = pair.getLeft();

        Method proxySetter = MethodUtils.getAccessibleMethod(proxyClass, setter.getName(), PARAMETER_TYPES);
        if (proxySetter != null) {
            return new BeanMethodInfo(bean, property, proxySetter);
        } else {
            return new BeanMethodInfo(originalBean, property, setter);
        }
    }

    private void updateProperty(String propertyName, String value) {
        final List<BeanMemberInfo> infoList = properties.get(propertyName);
        if (infoList == null) {
            log.warn("Cannot update property {}, no beans use this property.", propertyName);
            return;
        }

        for (BeanMemberInfo beanInfo : infoList) {
            beanInfo.setValue(value);
        }
    }
}
