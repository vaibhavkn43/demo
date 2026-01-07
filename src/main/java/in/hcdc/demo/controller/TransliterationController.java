package in.hcdc.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author Vaibhav
 */
@RestController
@RequestMapping("/api")
public class TransliterationController {

    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/transliterate")
    public ResponseEntity<String> transliterate(@RequestParam String text) {
         String url = "http://10.208.35.130:9000/transliterate?text=" + text;
        return ResponseEntity.ok(restTemplate.getForObject(url, String.class));
    }
}
