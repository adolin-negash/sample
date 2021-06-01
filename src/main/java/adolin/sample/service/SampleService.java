package adolin.sample.service;

import adolin.sample.model.EchoMessage;

/**
 * Сервис-образец.
 *
 * @author Adolin Negash 13.05.2021
 */
public interface SampleService {

    /**
     * Сервис-образец.
     *
     * @param data параметр для примера
     * @return {@link EchoMessage} образец dto-объекта.
     */
    EchoMessage echo(String data);
}
