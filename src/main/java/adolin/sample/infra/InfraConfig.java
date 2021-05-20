package adolin.sample.infra;

import adolin.sample.infra.updatable.DefaultUpdatableBeanRegistry;
import adolin.sample.infra.updatable.UpdatableAnnotationBeanPostProcessor;
import adolin.sample.infra.updatable.UpdatableBeanMemberInfoExtractor;
import adolin.sample.infra.updatable.UpdatableBeanRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация инфраструктурных бинов.
 *
 * @author Adolin Negash 14.05.2021
 */
@ConditionalOnMissingBean
@Configuration
public class InfraConfig {

    /**
     * Реестр обновляемых свойств.
     *
     * @return {@link DefaultUpdatableBeanRegistry}
     */
    @Bean
    public UpdatableBeanRegistry updatableBeanRegistry() {
        return new DefaultUpdatableBeanRegistry();
    }

    /**
     * Обработчик бинов, который добавляет в бины функционал обновляемых полей.
     *
     * @return {@link UpdatableAnnotationBeanPostProcessor}
     */
    @Bean
    public UpdatableAnnotationBeanPostProcessor updatableAnnotationBeanPostProcessor() {
        return new UpdatableAnnotationBeanPostProcessor();
    }

    /**
     * Обработчик, извлекающий из класса обновляемые поля и сеттеры.
     *
     * @return {@link UpdatableBeanMemberInfoExtractor}
     */
    @Bean
    public UpdatableBeanMemberInfoExtractor updatableBeanMemberInfoExtractor() {
        return new UpdatableBeanMemberInfoExtractor();
    }
}
