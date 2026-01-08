package in.hcdc.demo.model;

/**
 *
 * @author Vaibhav
 */
public interface TemplateRenderer<T extends TemplateForm> {
    String render(T form, String templateId);
}

