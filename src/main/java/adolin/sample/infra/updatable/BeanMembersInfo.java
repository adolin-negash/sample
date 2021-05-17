package adolin.sample.infra.updatable;

import adolin.sample.infra.annotations.UpdatableBean;
import adolin.sample.infra.annotations.UpdatableValue;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.tuple.Pair;

/**
 * < ... description>
 *
 * @author Adolin Negash 17.05.2021
 */
@Getter
@Setter
@Accessors(chain = true)
class BeanMembersInfo {

    private Object bean;

    private UpdatableBean annotation;

    private List<Pair<Field, UpdatableValue>> fields;

    private List<Pair<Method, UpdatableValue>> setters;
}
