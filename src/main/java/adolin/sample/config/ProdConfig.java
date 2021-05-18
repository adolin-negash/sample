package adolin.sample.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Настройки бинов, используемые в стандартном режиме.
 *
 * @author Adolin Negash 18.05.2021
 */
@Profile("prod")
@Configuration
public class ProdConfig extends CommonConfig {

}
