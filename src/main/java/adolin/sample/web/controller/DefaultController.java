package adolin.sample.web.controller;

import adolin.sample.model.EchoMessage;
import adolin.sample.service.SampleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
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

  /**
   * Echo.
   *
   * @param text text.
   * @return result echo text.
   */
  @GetMapping("/echo")
  public EchoMessage echo(@RequestParam("text") String text) {
    return sampleService.echo(text);
  }
}
