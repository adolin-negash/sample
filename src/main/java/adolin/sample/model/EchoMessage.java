package adolin.sample.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Echo message dto.
 *
 * @author Adolin Negash 12.05.2021
 */
@Getter
@Setter
@Accessors(chain = true)
public class EchoMessage {

    /**
     * Echo message.
     */
    private String message;

    /**
     *
     */
    private String info;
}
