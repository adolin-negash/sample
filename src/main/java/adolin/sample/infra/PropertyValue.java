package adolin.sample.infra;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Значение свойства.
 *
 * @author Adolin Negash 13.05.2021
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PropertyValue {

  /**
   * Название свойства.
   */
  private String name;

  /**
   * Значение свойства.
   */
  private String value;
}
