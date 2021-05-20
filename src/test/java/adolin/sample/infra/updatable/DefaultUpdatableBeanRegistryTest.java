package adolin.sample.infra.updatable;

import adolin.sample.AbstractMockTest;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.core.env.Environment;

/**
 * Класс для тестирования {@link DefaultUpdatableBeanRegistry}
 *
 * @author Adolin Negash 20.05.2021
 */
class DefaultUpdatableBeanRegistryTest extends AbstractMockTest {

    @InjectMocks
    private DefaultUpdatableBeanRegistry subj;

    @Mock
    private Environment environment;

    @Mock
    private UpdatableBeanMemberInfoExtractor infoExtractor;

    @Test
    void shouldRegisterBean() {

    }
}
