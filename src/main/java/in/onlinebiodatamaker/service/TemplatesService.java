package in.onlinebiodatamaker.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.onlinebiodatamaker.model.Template;
import jakarta.annotation.PostConstruct;
import java.io.InputStream;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

@Service
@Getter
@Setter
public class TemplatesService {

    private List<Template> templates;

    @PostConstruct
    public void loadTemplates() {
        try {
            ObjectMapper mapper = new ObjectMapper();

            InputStream is = getClass()
                    .getClassLoader()
                    .getResourceAsStream("config/templates.json");

            TemplatesWrapper wrapper = mapper.readValue(is, TemplatesWrapper.class);

            this.templates = wrapper.getTemplates();

        } catch (Exception e) {
            throw new RuntimeException("Failed to load templates.json", e);
        }
    }

    // =========================
    // BASIC METHODS
    // =========================

    public List<Template> getAllTemplates() {
        return templates;
    }

    public Template getById(String id) {
        return templates.stream()
                .filter(t -> t.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public List<String> getTemplateIds() {
        return templates.stream()
                .map(Template::getId)
                .toList();
    }

    // =========================
    // 🔥 NEW: FILTER BY RELIGION
    // =========================
    public List<Template> getByReligion(String religion) {

        if (religion == null || religion.isBlank()) {
            return templates;
        }

        return templates.stream()
                .filter(t -> religion.equalsIgnoreCase(t.getWhichReligion()))
                .toList();
    }

    // =========================
    // 🔥 NEW: GET DISTINCT RELIGIONS (for filter buttons)
    // =========================
    public Set<String> getAvailableReligions() {
        return templates.stream()
                .map(Template::getWhichReligion)
                .filter(r -> r != null && !r.isBlank())
                .map(String::toUpperCase)
                .collect(Collectors.toSet());
    }

    // =========================
    // 🔽 Inner Wrapper Class
    // =========================
    @Getter
    @Setter
    public static class TemplatesWrapper {
        private List<Template> templates;
    }
}