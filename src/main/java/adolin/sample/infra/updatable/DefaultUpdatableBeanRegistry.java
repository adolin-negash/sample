package adolin.sample.infra.updatable;

import adolin.sample.infra.annotations.UpdatableBean;
import adolin.sample.infra.annotations.UpdatableValue;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import static java.util.Collections.emptyList;
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

        private void callAfterUpdate() throws InvocationTargetException, IllegalAccessException {
            if (afterUpdateMethod != null) {
                afterUpdateMethod.invoke(bean);
            }
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
    public synchronized void registerBean(String beanName,
        Object bean,
        Object proxyBean,
        UpdatableBean annotation) {

        requireNonNull(beanName, "empty beanName");
        requireNonNull(bean, "empty bean");
        requireNonNull(proxyBean, "empty proxyBean");
        requireNonNull(annotation, "empty bean annotation");

        final Class<?> beanClass = bean.getClass();
        final Class<?> proxyBeanClass = proxyBean.getClass();

        final Stream<Pair<UpdatableValue, BeanMemberInfo>> fieldMembers = infoExtractor
            .extractUpdatableFields(beanClass)
            .map(pair -> Pair.of(pair.getRight(), new BeanFieldInfo(bean, beanName, pair.getLeft())));

        final Stream<Pair<UpdatableValue, BeanMemberInfo>> methodMembers = infoExtractor
            .extractUpdatableSetters(beanClass)
            .map(pair -> Pair.of(pair.getRight(),
                getBeanMemberInfo(beanName, proxyBean, bean, proxyBeanClass, pair.getLeft())));

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

            propertyInfo.addMember(pair.getRight());
            updateProperty(propertyName, propertyInfo.getValue());
        }

        String onUpdateMethodName = annotation.onUpdateMethod();
        Method onUpdateMethod = infoExtractor.extractOnUpdateMethod(proxyBeanClass, onUpdateMethodName);
        if (onUpdateMethod == null) {
            onUpdateMethod = infoExtractor.extractOnUpdateMethod(beanClass, onUpdateMethodName);
        }

        beans.put(beanName, new BeanInfo(bean, onUpdateMethod));
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
        final Set<String> beanNames = new HashSet<>();
        for (PropertyValue value : listOfValues) {
            final List<String> changedNames = updateProperty(value.getName(), value.getValue());
            beanNames.addAll(changedNames);
        }

        for (String name : beanNames) {
            beans.get(name).callAfterUpdate();
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
        final PropertyInfo info = properties.computeIfAbsent(propertyName, name -> new PropertyInfo(value));
        if (info.isEmpty()) {
            log.warn("No bean use property \"{}\".", propertyName);
            return emptyList();
        } else {
            info.setValue(value);
            return info.getBeanNames();
        }
    }
}
