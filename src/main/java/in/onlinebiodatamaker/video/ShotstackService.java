package in.onlinebiodatamaker.video;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author Vaibhav
 */
@Service
public class ShotstackService {

    @Value("${shotstack.apiKey}")
    private String apiKey;

    @Value("${shotstack.baseUrl}")
    private String baseUrl;

    private final RestTemplate restTemplate;

    public ShotstackService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // 🔥 CREATE VIDEO
    public String renderVideo(WeddingVideoRequest req) {

        Map<String, Object> payload = buildPayload(req);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-api-key", apiKey);

        HttpEntity<Map<String, Object>> request
                = new HttpEntity<>(payload, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                baseUrl + "/render",
                request,
                Map.class
        );

        Map body = response.getBody();
        Map res = (Map) body.get("response");

        return (String) res.get("id");   // renderId
    }

    private Map<String, Object> buildPayload(WeddingVideoRequest r) {

        Map<String, Object> timeline = new HashMap<>();

        List<Object> tracks = new ArrayList<>();

        // ---- TEXT TRACK ----
        tracks.add(Map.of("clips", List.of(
                textClip(r.getIntroTitle(), 0, 4, 60),
                textClip(r.getFamilyTitle(), 4, 4, 40),
                textClip(r.getBrideName() + " 💛 " + r.getGroomName(), 8, 6, 55),
                textClip("दिनांक: " + r.getWeddingDate() + "\nवेळ: " + r.getWeddingTime() + "\nस्थळ: " + r.getVenue(), 14, 6, 38),
                textClip(r.getInviteMessageLine1() + "\n" + r.getInviteMessageLine2(), 28, 5, 42),
                textClip("RSVP: " + r.getContactNumber(), 33, 5, 36)
        )));

        // ---- IMAGE TRACK ----
        tracks.add(Map.of("clips", List.of(
                imageClip(r.getPhoto1(), 20, 2.5),
                imageClip(r.getPhoto2(), 22.5, 2.5),
                imageClip(r.getPhoto3(), 25, 3)
        )));

        // ---- AUDIO TRACK ----
        tracks.add(Map.of("clips", List.of(
                audioClip(r.getMusicTrack(), 0, 38)
        )));

        timeline.put("background", "#8B0000");
        timeline.put("tracks", tracks);

        Map<String, Object> output = Map.of(
                "format", "mp4",
                "resolution", "hd",
                "aspectRatio", "16:9"
        );

        return Map.of("timeline", timeline, "output", output);
    }

    private Map<String, Object> textClip(String text, double start, double length, int size) {
        return Map.of(
                "asset", Map.of(
                        "type", "text",
                        "text", text,
                        "font", Map.of(
                                "family", "Noto Sans Devanagari",
                                "color", "#FFD700",
                                "size", size
                        )
                ),
                "start", start,
                "length", length,
                "transition", Map.of(
                        "in", "fade",
                        "out", "fade"
                )
        );
    }

    private Map<String, Object> imageClip(String url, double start, double length) {
        return Map.of(
                "asset", Map.of("type", "image", "src", url),
                "start", start,
                "length", length,
                "fit", "cover"
        );
    }

    private Map<String, Object> audioClip(String url, double start, double length) {
        return Map.of(
                "asset", Map.of("type", "audio", "src", url),
                "start", start,
                "length", length
        );
    }

    public Map getRenderStatus(String id) {

        HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-key", apiKey);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                baseUrl + "/render/" + id,
                HttpMethod.GET,
                request,
                Map.class
        );

        return response.getBody();
    }

}
