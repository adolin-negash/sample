package adolin.sample.infra;

import java.util.List;

/**
 * < ... description>
 *
 * @author Adolin Negash 13.05.2021
 */
public interface UpdatableBeanRegistry {

  /**
   * @param propertyName
   * @return
   */
  UpdatableProperty getProperty(String propertyName);

  /**
   *
   */
  void updateProperties(List<PropertyValue> values);
}
