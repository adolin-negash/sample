package adolin.sample.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Настройки бинов, используемые в режиме тестов.
 *
 * @author Adolin Negash 13.05.2021
 */
@Configuration
@Profile("test")
public class TestConfig extends CommonConfig {

}
