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
class MediaItem {
    private boolean enabled;
    private boolean optional;
    private String position;
    private int height; // or size
}

