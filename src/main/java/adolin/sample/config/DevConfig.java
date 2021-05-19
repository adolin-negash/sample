package adolin.sample.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Настройки бинов, используемые в режиме разработки (dev).
 *
 * @author Adolin Negash 12.05.2021
 */
@Configuration
@Profile("dev")
public class DevConfig extends CommonConfig {
}
