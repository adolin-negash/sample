package adolin.sample.infra.updatable;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 *
 */
@Getter
@RequiredArgsConstructor
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
  private final List<BeanFieldInfo> beans = new ArrayList<>();
}
