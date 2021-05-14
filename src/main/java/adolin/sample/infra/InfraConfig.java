package adolin.sample.infra;

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
   * Инициализатор бинов с обновляемыми свойствами.
   *
   * @return {@link UpdatableFieldsInitializer}
   */
  @Bean
  public UpdatableFieldsInitializer updatableFieldsInitializer() {
    return new UpdatableFieldsInitializer();
  }

  /**
   * Реестр обновляемых свойств.
   *
   * @return {@link DefaultUpdatableBeanRegistry}
   */
  @Bean
  public UpdatableBeanRegistry updatableBeanRegistry() {
    return new DefaultUpdatableBeanRegistry();
  }
}
