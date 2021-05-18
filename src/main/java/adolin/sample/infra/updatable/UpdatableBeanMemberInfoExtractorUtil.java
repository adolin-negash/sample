package adolin.sample.infra.updatable;

import adolin.sample.infra.annotations.UpdatableValue;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.stream.Stream;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.apache.commons.lang3.tuple.Pair;

/**
 * < ... description>
 *
 * @author Adolin Negash 17.05.2021
 */
class UpdatableBeanMemberInfoExtractorUtil {

    private UpdatableBeanMemberInfoExtractorUtil() {
    }

    /**
     * @param beanClass
     * @return
     */
    static Stream<Pair<Field, UpdatableValue>> extractUpdatableFields(Class<?> beanClass) {
        return FieldUtils.getAllFieldsList(beanClass).stream()
            .filter(UpdatableBeanMemberInfoExtractorUtil::isFieldValid)
            .map(field -> Pair.of(field, field.getAnnotation(UpdatableValue.class)))
            .filter(pair -> pair.getRight() != null);
    }

    /**
     * @param beanClass
     * @return
     */
    static Stream<Pair<Method, UpdatableValue>> extractUpdatableSetters(Class<?> beanClass) {
        return MethodUtils.getMethodsListWithAnnotation(beanClass, UpdatableValue.class, true, true)
            .stream()
            .filter(UpdatableBeanMemberInfoExtractorUtil::isValidSetter)
            .map(method -> Pair.of(method, method.getAnnotation(UpdatableValue.class)))
            .filter(pair -> pair.getRight() != null);
    }

    private static boolean isFieldValid(Field field) {
        final Class<?> type = field.getType();
        final int modifiers = field.getModifiers();

        return type.isAssignableFrom(String.class)
            && !Modifier.isFinal(modifiers)
            && !Modifier.isStatic(modifiers);
    }

    private static boolean isValidSetter(Method method) {
        Class<?>[] params = method.getParameterTypes();
        if (params.length != 1) {
            return false;
        }
        return params[0].isAssignableFrom(String.class);
    }

}
