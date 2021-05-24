package adolin.sample;

import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

/**
 * Точка входа в приложение.
 *
 * @author Adolin Negash 12.05.2021
 */
@SpringBootApplication
@Slf4j
public class Application {

    @Autowired
    private Environment environment;

    /**
     * Точка входа в приложение.
     *
     * @param args command line args.
     */
    public static void main(String[] args) {
        new SpringApplicationBuilder(Application.class)
            .web(WebApplicationType.SERVLET)
            .run(args);
    }

    /**
     * Prompts common info when application is ready.
     *
     * @return {@link CommandLineRunner}
     */
    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> log.info("Success. Profiles: {}", Arrays.asList(environment.getActiveProfiles()));
    }
}
