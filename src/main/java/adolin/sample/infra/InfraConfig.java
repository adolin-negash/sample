package adolin.sample.infra;

import adolin.sample.infra.updatable.DefaultUpdatableBeanRegistry;
import adolin.sample.infra.updatable.UpdatableBeanRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация инфраструктурных бинов.
 *
 * @author Adolin Negash 14.05.2021
 */
@Configuration
public class InfraConfig {

    /**
     * Реестр обновляемых свойств.
     *
     * @return {@link DefaultUpdatableBeanRegistry}
     */
    @ConditionalOnMissingBean
    @Bean
    public UpdatableBeanRegistry updatableBeanRegistry() {
        return new DefaultUpdatableBeanRegistry();
    }
}
