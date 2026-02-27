package in.onlinebiodatamaker.video;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Vaibhav
 */
@RestController
@RequestMapping("/api/video")
public class WeddingVideoController {

    private final ShotstackService shotstackService;

    public WeddingVideoController(ShotstackService shotstackService) {
        this.shotstackService = shotstackService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createVideo(@RequestBody WeddingVideoRequest request) {

        String renderId = shotstackService.renderVideo(request);

        return ResponseEntity.ok(Map.of(
                "status", "PROCESSING",
                "renderId", renderId
        ));
    }

    @GetMapping("/status/{id}")
    public ResponseEntity<?> status(@PathVariable String id) {
        return ResponseEntity.ok(shotstackService.getRenderStatus(id));
    }
}
