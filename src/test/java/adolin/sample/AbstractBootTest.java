package adolin.sample;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Базовый класс для тестов, использующих spring-конфигурацию
 *
 * @author Adolin Negash 12.05.2021
 */
@ActiveProfiles("test")
@SpringBootTest
public abstract class AbstractBootTest {

}
