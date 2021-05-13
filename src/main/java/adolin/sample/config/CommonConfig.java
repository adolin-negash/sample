package adolin.sample.config;

import adolin.sample.service.SampleService;
import adolin.sample.service.SampleServiceImpl;
import org.springframework.context.annotation.Bean;

/**
 * Common bean configuration.
 *
 * @author Adolin Negash 12.05.2021
 */
public class CommonConfig {

  /**
   * @return
   */
  @Bean
  public SampleService sampleService() {
    return new SampleServiceImpl();
  }
}
