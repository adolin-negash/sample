package adolin.sample.infra.updatable;

import java.lang.reflect.Field;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Класс для тестирования {@link BeanFieldInfo}
 *
 * @author Adolin Negash 19.05.2021
 */
class BeanFieldInfoTest {

    @SuppressWarnings("unused")
    static class TestClass {

        private String data;
    }

    @Test
    void shouldSetValue() {
        final Field field = TestClass.class.getDeclaredFields()[0];
        final TestClass testClass = new TestClass();
        final BeanFieldInfo info = new BeanFieldInfo(testClass, "bean", field);
        final String someData = "some data";

        info.setValue(someData);
        assertEquals(someData, testClass.data);
    }
}
