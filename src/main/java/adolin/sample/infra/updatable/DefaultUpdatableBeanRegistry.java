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
import lombok.Getter;
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
    private static class BeanInfo {

        private final Object bean;

        private final Method afterUpdateMethod;

        BeanInfo(Object bean, Method afterUpdateMethod) {
            this.bean = bean;
            this.afterUpdateMethod = afterUpdateMethod;
        }
    }

    @Getter
    private static class PropertyInfo {

        @Setter
        private String value;

        private final List<BeanMemberInfo> members = new ArrayList<>();
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
        final Class<?> proxyClass = proxyBean.getClass();

        Stream<Pair<UpdatableValue, BeanMemberInfo>> fieldMembers = infoExtractor.extractUpdatableFields(beanClass)
            .map(pair -> Pair.of(pair.getRight(), new BeanFieldInfo(bean, beanName, pair.getLeft())));

        Stream<Pair<UpdatableValue, BeanMemberInfo>> methodMembers = infoExtractor.extractUpdatableSetters(beanClass)
            .map(pair -> Pair.of(pair.getRight(),
                getBeanMemberInfo(beanName, proxyBean, bean, proxyClass, pair.getLeft())));

        Stream.concat(fieldMembers, methodMembers)
            .forEach(pair -> {
                final String propertyName = pair.getLeft().value();
                if (StringUtils.isBlank(propertyName)) {
                    throw new IllegalArgumentException("empty property in UpdatableValue annotation");
                }

                final PropertyInfo propertyInfo = properties.computeIfAbsent(propertyName, n -> new PropertyInfo());
                final BeanMemberInfo memberInfo = pair.getRight();
                propertyInfo.getMembers().add(memberInfo);
                updateProperty(propertyName, environment.getProperty(propertyName));
            });
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

        final Method proxySetter = getAccessibleMethod(proxyClass, setter.getName(), SETTER_SIGNATURE);
        if (proxySetter != null) {
            return new BeanMethodInfo(proxyBean, beanName, proxySetter);
        } else {
            return new BeanMethodInfo(originalBean, beanName, setter);
        }
    }

    private List<String> updateProperty(String propertyName, String value) {
        final PropertyInfo info = properties.get(propertyName);
        final List<String> beanNames = new ArrayList<>();
        if (info == null) {
            log.warn("Cannot update property {}, no beans use this property.", propertyName);
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
