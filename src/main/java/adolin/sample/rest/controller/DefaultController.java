package adolin.sample.rest.controller;

import adolin.sample.infra.updatable.PropertyValue;
import adolin.sample.infra.updatable.UpdatableBeanRegistry;
import adolin.sample.model.EchoMessage;
import adolin.sample.service.SampleService;
import java.util.Collection;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Default controller.
 *
 * @author Adolin Negash 12.05.2021
 */
@RestController
@RequestMapping("/api")
public class DefaultController {

    @Autowired
    private SampleService sampleService;

    @Autowired
    private UpdatableBeanRegistry updatableBeanRegistry;

    /**
     * Echo-сервис.
     *
     * @param text текст.
     * @return result echo-объект.
     */
    @GetMapping("/echo")
    public EchoMessage echo(@RequestParam("text") String text) {
        return sampleService.echo(text);
    }

    /**
     * Изменияет свойства в бинах.
     *
     * @param values список пар свойство-значение
     * @throws Exception ошибка при изменении свойств.
     */
    @PostMapping("/props")
    public void setProperties(@RequestBody List<PropertyValue> values) throws Exception {
        updatableBeanRegistry.updateProperties(values);
    }

    /**
     * Возвращает список свойств.
     *
     * @return список свойств.
     */
    @GetMapping("/props")
    public Collection<PropertyValue> getProperties() {
        return updatableBeanRegistry.getProperties();
    }
}
