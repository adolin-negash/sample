package adolin.sample.service;

import adolin.sample.infra.annotations.UpdatableValue;
import adolin.sample.model.EchoMessage;

/**
 * Сервис-образец.
 *
 * @author Adolin Negash 13.05.2021
 */
public class SampleServiceImpl implements SampleService {

    @UpdatableValue("adolin.sample.service.some-info")
    private String info;

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
            .setInfo(info);
    }
}
