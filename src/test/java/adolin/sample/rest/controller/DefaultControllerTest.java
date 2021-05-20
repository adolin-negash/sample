package adolin.sample.rest.controller;

import adolin.sample.AbstractRestTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Класс для тестирования {@link DefaultController}
 *
 * @author Adolin Negash 20.05.2021
 */
class DefaultControllerTest extends AbstractRestTest {

    @Autowired
    private DefaultController defaultController;

    @Test
    void shouldCallEchoService() {
        assertNotNull(defaultController);
    }
}
