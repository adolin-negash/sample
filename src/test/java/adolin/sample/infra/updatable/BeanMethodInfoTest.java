package adolin.sample.infra.updatable;

import java.lang.reflect.Method;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
    }

    @Test
    void shouldSetValue() {
        Method setter = MethodUtils.getMatchingMethod(TestClass.class, "setter", String.class);
        final TestClass testClass = new TestClass();
        final BeanMethodInfo info = new BeanMethodInfo(testClass, "bean", setter);

        final String someData = "some data";
        info.setValue(someData);
        assertEquals(someData, testClass.data);
    }
}
