package adolin.sample.service;

import adolin.sample.infra.annotations.UpdatableValue;
import adolin.sample.model.EchoMessage;
import lombok.extern.slf4j.Slf4j;

/**
 * Сервис-образец.
 *
 * @author Adolin Negash 13.05.2021
 */
@Slf4j
public class SampleServiceImpl implements SampleService {

    private String info;

    @UpdatableValue("adolin.sample.service.some-info")
    private String info2;

    /**
     * Сервис-образец.
     *
     * @param data параметр для примера
     * @return {@link EchoMessage} образец dto-объекта.
     */
    @Override
    public EchoMessage echo(String data) {
        return new EchoMessage()
            .setMessage(data)
            .setInfo(info)
            .setInfo2(info2);
    }

    @UpdatableValue("adolin.sample.service.some-info")
    public void setInfo(String info) {
        log.info("setInfo({})", info);
        this.info = info;
    }
}
