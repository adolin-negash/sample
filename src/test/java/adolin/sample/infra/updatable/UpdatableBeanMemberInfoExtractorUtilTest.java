package adolin.sample.infra.updatable;

import adolin.sample.AbstractMockTest;
import adolin.sample.infra.annotations.UpdatableValue;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;

import static adolin.sample.infra.updatable.UpdatableBeanMemberInfoExtractorUtil.extractUpdatableFields;
import static adolin.sample.infra.updatable.UpdatableBeanMemberInfoExtractorUtil.extractUpdatableSetters;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Тестовый клас для {@link UpdatableBeanMemberInfoExtractorUtil}
 *
 * @author Adolin Negash 18.05.2021
 */
class UpdatableBeanMemberInfoExtractorUtilTest extends AbstractMockTest {

    @SuppressWarnings("unused")
    private static class FieldTester {

        private String notAnnotated;

        @UpdatableValue("annotatedStringField")
        private String annotatedStringField;

        @UpdatableValue("annotatedStringField1")
        private String annotatedStringField1;

        @UpdatableValue("annotatedObjectField")
        private Object annotatedObjectField;

        @UpdatableValue("annotatedListField")
        private List<?> annotatedListField;

        @UpdatableValue("")
        private String annotatedFieldWithNoProperty;

        @UpdatableValue("staticField")
        private static String staticField;

        @UpdatableValue("finalField")
        private final String finalField = "";

        @UpdatableValue("staticFinalField")
        private static String staticFinalField;
    }

    @SuppressWarnings("unused")
    private static class SettersTester {

        @UpdatableValue("setSimple")
        private void setSimple(String data) {
        }

        @UpdatableValue("")
        private void setEmptyParamName(String data) {
        }

        @UpdatableValue("setWithReturn")
        private Object setWithReturn(String data) {
            return null;
        }

        @UpdatableValue("setWithTwoParams")
        private Object setWithTwoParams(String data, String param) {
            return null;
        }

        @UpdatableValue("setObjectParam")
        private void setObjectParam(Object data) {
        }

        @UpdatableValue("setListParam")
        private void setListParam(List<String> data) {
        }

        @UpdatableValue("setNoParams")
        private void setNoParams() {
        }

        private void setNoAnnotation(String data) {
        }

        @UpdatableValue("setStatic")
        private static void setStatic(Object data) {
        }
    }

    @Test
    void shouldExtractFieldsFromClass() {

        Stream<Pair<Field, UpdatableValue>> pairs = extractUpdatableFields(FieldTester.class);

        assertNotNull(pairs);

        final List<String> list = pairs
            .peek(pair -> assertEquals(pair.getLeft().getName(), pair.getRight().value()))
            .map(pair -> pair.getLeft().getName())
            .sorted()
            .collect(Collectors.toList());

        final List<String> expected = asList("annotatedObjectField", "annotatedStringField", "annotatedStringField1");
        expected.sort(StringUtils::compare);

        assertIterableEquals(expected, list);
    }

    @Test
    void shouldExtractSettersFromClass() {
        Stream<Pair<Method, UpdatableValue>> pairs = extractUpdatableSetters(SettersTester.class);

        assertNotNull(pairs);

        final List<String> list = pairs
            .peek(pair -> assertEquals(pair.getLeft().getName(), pair.getRight().value()))
            .map(pair -> pair.getLeft().getName())
            .sorted()
            .collect(Collectors.toList());

        final List<String> expected = asList("setSimple", "setWithReturn", "setObjectParam");
        expected.sort(StringUtils::compare);

        assertIterableEquals(expected, list);
    }
}
