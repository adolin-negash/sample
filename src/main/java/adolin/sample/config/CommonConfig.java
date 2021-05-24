package adolin.sample.config;

import adolin.sample.service.SampleService;
import adolin.sample.service.SampleServiceImpl;
import adolin.starter.annotations.UpdatableBean;
import org.springframework.cache.annotation.EnableCaching;

/**
 * Общая конфигурация бинов.
 *
 * @author Adolin Negash 12.05.2021
 */
@EnableCaching
public class CommonConfig {

    /**
     * Сервис-образец.
     *
     * @return {@link SampleServiceImpl}
     */
    @UpdatableBean
    public SampleService sampleService() {
        return new SampleServiceImpl();
    }
}