package adolin.sample.infra;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 *
 */
@Getter
public class UpdatableProperty {

  /**
   *
   */
  private final String name;

  /**
   *
   */
  @Setter
  private String value;

  /**
   *
   */
  private final List<BeanInfo> beans = new ArrayList<>();

  /**
   * @param name
   */
  public UpdatableProperty(String name) {
    this.name = name;
  }
}
