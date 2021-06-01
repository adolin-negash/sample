package adolin.sample.rest.controller;

import adolin.sample.AbstractRestTest;
import adolin.sample.model.EchoMessage;
import adolin.sample.service.SampleService;
import adolin.starter.updatable.PropertyValue;
import adolin.starter.updatable.UpdatableBeanRegistry;
import com.fasterxml.jackson.databind.type.CollectionType;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Класс для тестирования {@link DefaultController}
 *
 * @author Adolin Negash 20.05.2021
 */
@WebMvcTest(DefaultController.class)
class DefaultControllerTest extends AbstractRestTest {

    @Autowired
    private DefaultController defaultController;

    @MockBean
    private SampleService sampleService;

    @MockBean
    private UpdatableBeanRegistry updatableBeanRegistry;

    @Captor
    private ArgumentCaptor<List<PropertyValue>> listCaptor;

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(sampleService, updatableBeanRegistry);
    }

    @Test
    void shouldCallEchoService() throws Exception {

        final String paramValue = "ABCD";

        final EchoMessage expected = new EchoMessage()
            .setMessage("message")
            .setInfo("info")
            .setInfo2("info2");

        when(sampleService.echo(paramValue)).thenReturn(expected);

        final MvcResult result = mockMvc.perform(get("/api/echo")
            .param("text", paramValue)
            .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk())
            .andReturn();

        verify(sampleService).echo(eq(paramValue));

        final EchoMessage message = objectMapper
            .readValue(result.getResponse().getContentAsString(), EchoMessage.class);

        assertEquals("message", message.getMessage());
        assertEquals("info", message.getInfo());
        assertEquals("info2", message.getInfo2());
    }

    @Test
    void shouldGetProperties() throws Exception {

        final List<PropertyValue> expected = getStubPropertyValues();
        when(updatableBeanRegistry.getProperties()).thenReturn(expected);

        final MvcResult result = mockMvc.perform(get("/api/props")
            .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk())
            .andReturn();

        verify(updatableBeanRegistry).getProperties();

        final CollectionType type = objectMapper.getTypeFactory()
            .constructCollectionType(List.class, PropertyValue.class);

        final List<PropertyValue> values = objectMapper.readValue(result.getResponse().getContentAsString(), type);

        assertPropertyValues(expected, values);
    }

    @Test
    void shouldSetProperties() throws Exception {
        final List<PropertyValue> input = getStubPropertyValues();

        mockMvc.perform(post("/api/props")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsBytes(input)))
            .andExpect(status().isOk());

        verify(updatableBeanRegistry).updateProperties(listCaptor.capture());

        assertPropertyValues(input, listCaptor.getValue());
    }

    private List<PropertyValue> getStubPropertyValues() {
        return Arrays.asList(
            new PropertyValue("name1", "val1"),
            new PropertyValue("name2", "val2")
        );
    }

    private void assertPropertyValues(List<PropertyValue> expected, List<PropertyValue> values) {
        assertNotNull(values);
        assertEquals(expected.size(), values.size());

        for (int i = 0; i < values.size(); i++) {
            final PropertyValue expectedValue = expected.get(i);
            final PropertyValue value = values.get(i);
            assertEquals(Objects.isNull(expectedValue), Objects.isNull(value));

            if (value != null) {
                assertEquals(expectedValue.getName(), value.getName());
                assertEquals(expectedValue.getValue(), value.getValue());
            }
        }
    }

}
