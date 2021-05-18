package adolin.sample.infra.updatable;

import adolin.sample.infra.annotations.UpdatableBean;
import adolin.sample.infra.annotations.UpdatableValue;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import static adolin.sample.infra.updatable.UpdatableBeanMemberInfoExtractorUtil.extractUpdatableFields;
import static adolin.sample.infra.updatable.UpdatableBeanMemberInfoExtractorUtil.extractUpdatableSetters;

/**
 * Реестр обновляемых свойств. Хранит свойства и обновляет их в привязанных бинах.
 *
 * @author Adolin Negash 14.05.2021
 */
@Slf4j
public class DefaultUpdatableBeanRegistry implements UpdatableBeanRegistry {

    private static final Class<?>[] SETTER_SIGNATURE = {String.class};

    @Getter
    static class BeanInfo {

        private final Object bean;

        private final Method afterUpdateMethod;

        BeanInfo(Object bean, Method afterUpdateMethod) {
            this.bean = bean;
            this.afterUpdateMethod = afterUpdateMethod;
        }
    }

    private final ConcurrentHashMap<String, List<BeanMemberInfo>> properties = new ConcurrentHashMap<>();

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

        final Class<?> beanClass = bean.getClass();
        final Class<?> proxyClass = proxyBean.getClass();

        Stream<Pair<UpdatableValue, BeanMemberInfo>> fieldMembers = extractUpdatableFields(beanClass)
            .map(pair -> Pair.of(pair.getRight(), new BeanFieldInfo(beanClass, beanName, pair.getLeft())));

        Stream<Pair<UpdatableValue, BeanMemberInfo>> methodMembers = extractUpdatableSetters(beanClass)
            .map(pair -> Pair
                .of(pair.getRight(), getBeanMemberInfo(beanName, proxyBean, bean, proxyClass, pair.getLeft())));

        Stream.concat(fieldMembers, methodMembers)
            .forEach(pair -> {
                final String propertyName = pair.getLeft().value();
                if (StringUtils.isBlank(propertyName)) {
                    throw new IllegalArgumentException("empty property in UpdatableValue annotation");
                }

                final List<BeanMemberInfo> membersList = properties
                    .computeIfAbsent(propertyName, n -> new ArrayList<>());
                membersList.add(pair.getRight());
            });
        beans.put(beanName, new BeanInfo(bean, null));
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
    public void updateProperties(List<PropertyValue> listOfValues) throws Exception {
        Objects.requireNonNull(listOfValues, "empty list");
        HashSet<String> beanNames = new HashSet<>();
        for (PropertyValue value : listOfValues) {
            List<String> changedNames = updateProperty(value.getName(), value.getValue());
            beanNames.addAll(changedNames);
        }

        for (String name : beanNames) {
            BeanInfo info = beans.get(name);
            Method updateMethod = info.getAfterUpdateMethod();
            if (updateMethod != null) {
                updateMethod.invoke(info.getBean());
            }
        }
    }

    private BeanMemberInfo getBeanMemberInfo(String beanName,
        Object proxyBean,
        Object originalBean,
        Class<?> proxyClass,
        Method setter) {

        final Method proxySetter = MethodUtils.getAccessibleMethod(proxyClass, setter.getName(), SETTER_SIGNATURE);
        if (proxySetter != null) {
            return new BeanMethodInfo(proxyBean, beanName, proxySetter);
        } else {
            return new BeanMethodInfo(originalBean, beanName, setter);
        }
    }

    private List<String> updateProperty(String propertyName, String value) {
        final List<BeanMemberInfo> infoList = properties.get(propertyName);
        final List<String> beanNames = new ArrayList<>();
        if (infoList == null) {
            log.warn("Cannot update property {}, no beans use this property.", propertyName);
            return beanNames;
        }

        for (BeanMemberInfo beanInfo : infoList) {
            beanInfo.setValue(value);
            beanNames.add(beanInfo.getBeanName());
        }
        return beanNames;
    }
}
