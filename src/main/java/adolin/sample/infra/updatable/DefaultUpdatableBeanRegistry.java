package adolin.sample.infra.updatable;

import adolin.sample.infra.annotations.UpdatableBean;
import adolin.sample.infra.annotations.UpdatableValue;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.reflect.MethodUtils.getAccessibleMethod;

/**
 * Реестр обновляемых свойств. Хранит свойства и обновляет их в привязанных бинах.
 *
 * @author Adolin Negash 14.05.2021
 */
@Slf4j
public class DefaultUpdatableBeanRegistry implements UpdatableBeanRegistry {

    private static final Class<?>[] SETTER_SIGNATURE = {String.class};

    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static class BeanInfo {

        private final Object bean;

        private final Method afterUpdateMethod;
    }

    @Getter
    private static class PropertyInfo {

        @Setter
        private String value;

        private final List<BeanMemberInfo> members = new ArrayList<>();

        public PropertyInfo(String value) {
            this.value = value;
        }
    }

    private final ConcurrentHashMap<String, PropertyInfo> properties = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<String, BeanInfo> beans = new ConcurrentHashMap<>();

    @Autowired
    private Environment environment;

    @Autowired
    private UpdatableBeanMemberInfoExtractor infoExtractor;

    /**
     * Регистрирует в реестре бин с обновляемыми свойствами.
     *
     * @param beanName   имя бина.
     * @param bean       бин.
     * @param proxyBean  запроксированный бин.
     * @param annotation аннотация.
     */
    @Override
    public synchronized void registerBean(String beanName, Object bean, Object proxyBean, UpdatableBean annotation) {
        requireNonNull(beanName, "empty beanName");
        requireNonNull(bean, "empty bean");
        requireNonNull(proxyBean, "empty proxyBean");
        requireNonNull(annotation, "empty bean annotation");

        final Class<?> beanClass = bean.getClass();

        final Stream<Pair<UpdatableValue, BeanMemberInfo>> fieldMembers = infoExtractor
            .extractUpdatableFields(beanClass)
            .map(pair -> Pair.of(pair.getRight(), new BeanFieldInfo(bean, beanName, pair.getLeft())));

        final Stream<Pair<UpdatableValue, BeanMemberInfo>> methodMembers = infoExtractor
            .extractUpdatableSetters(beanClass)
            .map(pair -> Pair.of(pair.getRight(),
                getBeanMemberInfo(beanName, proxyBean, bean, proxyBean.getClass(), pair.getLeft())));

        final List<Pair<UpdatableValue, BeanMemberInfo>> members = Stream.concat(fieldMembers, methodMembers)
            .collect(Collectors.toList());

        if (members.isEmpty()) {
            return;
        }

        for (Pair<UpdatableValue, BeanMemberInfo> pair : members) {
            final String propertyName = pair.getLeft().value();
            if (StringUtils.isBlank(propertyName)) {
                throw new IllegalArgumentException("empty property in UpdatableValue annotation");
            }

            final PropertyInfo propertyInfo = properties.computeIfAbsent(propertyName,
                name -> new PropertyInfo(environment.getProperty(name)));

            propertyInfo.getMembers().add(pair.getRight());
            updateProperty(propertyName, propertyInfo.getValue());
        }
        beans.put(beanName, new BeanInfo(bean, null));
    }

    /**
     * Возвращает список свойств.
     *
     * @return список свойств.
     */
    @Override
    public synchronized Collection<PropertyValue> getProperties() {
        return properties.entrySet().stream()
            .map(es -> new PropertyValue(es.getKey(), es.getValue().getValue()))
            .collect(Collectors.toList());
    }

    /**
     * Обновляет заданные свойства.
     *
     * @param listOfValues список обновляемых свойств и их значений.
     */
    @Override
    public synchronized void updateProperties(Collection<PropertyValue> listOfValues) throws Exception {
        requireNonNull(listOfValues, "empty list");
        final HashSet<String> beanNames = new HashSet<>();
        for (PropertyValue value : listOfValues) {
            final List<String> changedNames = updateProperty(value.getName(), value.getValue());
            beanNames.addAll(changedNames);
        }

        for (String name : beanNames) {
            final BeanInfo info = beans.get(name);
            final Method updateMethod = info.getAfterUpdateMethod();
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

        final Method proxySetter = getAccessibleMethod(proxyClass, setter.getName(), SETTER_SIGNATURE);
        if (proxySetter != null) {
            return new BeanMethodInfo(proxyBean, beanName, proxySetter);
        } else {
            return new BeanMethodInfo(originalBean, beanName, setter);
        }
    }

    private List<String> updateProperty(String propertyName, String value) {
        final List<String> beanNames = new ArrayList<>();

        final PropertyInfo info = properties.get(propertyName);
        if (info == null) {
            log.warn("Cannot update property {}, no bean use this property.", propertyName);

            properties.put(propertyName, new PropertyInfo(value));

            return beanNames;
        }

        for (BeanMemberInfo beanInfo : info.getMembers()) {
            beanInfo.setValue(value);
            beanNames.add(beanInfo.getBeanName());
        }
        info.setValue(value);
        return beanNames;
    }
}
