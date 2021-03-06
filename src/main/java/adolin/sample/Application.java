package adolin.sample;

import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

/**
 * Entry point.
 *
 * @author Adolin Negash 12.05.2021
 */
@SpringBootApplication
@Slf4j
public class Application {

  @Autowired
  private Environment environment;

  /**
   * Entry point method.
   *
   * @param args command line args.
   */
  public static void main(String[] args) {
    SpringApplication.run(Application.class);
  }

  /**
   * Prompts common info when application is ready.
   *
   * @return {@link CommandLineRunner}
   */
  @Bean
  public CommandLineRunner commandLineRunner() {
    return args -> log
        .info("Success. Profiles: {}", Arrays.asList(environment.getActiveProfiles()));
  }
}
