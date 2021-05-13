package adolin.sample.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Dev bean configuration.
 *
 * @author Adolin Negash 12.05.2021
 */
@Configuration
@Profile("dev")
public class DevConfig extends CommonConfig {

}
