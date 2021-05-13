package adolin.sample.service;

import adolin.sample.model.EchoMessage;

/**
 * <Description>
 *
 * @author Adolin Negash 13.05.2021
 */
public interface SampleService {

  /**
   *
   * @param data
   * @return
   */
  EchoMessage echo(String data);
}
