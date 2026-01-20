package in.hcdc.demo.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author Vaibhav
 */
@Getter
@Setter
@ToString
public class MediaItem {

    private boolean enabled;     // template allows this media
    private boolean optional;    // user may skip
    private String position;     // TOP_CENTER | RIGHT | LEFT
    private int width;
    private int height;
    private int marginTop;
    private int marginBottom;
}


