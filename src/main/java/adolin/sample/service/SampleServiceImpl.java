package adolin.sample.service;

import adolin.sample.model.EchoMessage;

/**
 * < ... description>
 *
 * @author Adolin Negash 13.05.2021
 */
public class SampleServiceImpl implements SampleService {

  /**
   *
   * @param data
   * @return
   */
  @Override
  public EchoMessage echo(String data) {
    return new EchoMessage().setMessage(data);
  }
}
