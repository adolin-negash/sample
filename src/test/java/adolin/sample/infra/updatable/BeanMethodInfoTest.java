package adolin.sample.infra.updatable;

import java.lang.reflect.Method;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Класс для тестирования {@link BeanMethodInfo}
 *
 * @author Adolin Negash 19.05.2021
 */
class BeanMethodInfoTest {

    @SuppressWarnings("unused")
    static class TestClass {

        private String data;

        private void setter(String data) {
            this.data = data;
        }

        private void setterWithException(String data) {
            throw new RuntimeException("some error");
        }
    }

    private static final Method SETTER = getMethod("setter");

    private static final Method SETTER_WITH_EXCEPTION = getMethod("setterWithException");

    private static final String SOME_DATA = "some data";

    static Method getMethod(String name) {
        return MethodUtils.getMatchingMethod(TestClass.class, name, String.class);
    }

    @Test
    void shouldGetBeanInfo() {
        final TestClass bean = new TestClass();
        final BeanMethodInfo info = new BeanMethodInfo(bean, "bean", SETTER);
        assertEquals("bean", info.getBeanName());
        assertSame(bean, info.getBean());
    }

    @Test
    void shouldSetValue() {
        final TestClass bean = new TestClass();
        final BeanMethodInfo info = new BeanMethodInfo(bean, "bean", SETTER);

        info.setValue(SOME_DATA);
        assertEquals(SOME_DATA, bean.data);
    }

    @Test
    void shouldThrowOnThrowSetter() {

        final String oldData = "old data";

        final TestClass bean = new TestClass();

        final BeanMethodInfo info = new BeanMethodInfo(bean, "bean", SETTER_WITH_EXCEPTION);

        bean.data = oldData;
        info.setValue(SOME_DATA);
        assertEquals(oldData, bean.data);
    }
}
