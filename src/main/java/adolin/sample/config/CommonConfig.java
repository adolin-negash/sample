package adolin.sample.config;

import adolin.sample.infra.annotations.UpdatableBean;
import adolin.sample.service.SampleService;
import adolin.sample.service.SampleServiceImpl;

/**
 * Общая конфигурация бинов.
 *
 * @author Adolin Negash 12.05.2021
 */
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