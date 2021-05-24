package adolin.sample;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Базовый класс для тестирования REST-сервисов.
 *
 * @author Adolin Negash 20.05.2021
 */
@ActiveProfiles("test")
public abstract class AbstractRestTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

}
