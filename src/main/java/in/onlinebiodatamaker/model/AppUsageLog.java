package in.onlinebiodatamaker.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "app_usage_logs")
public class AppUsageLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // request info
    private String ipAddress;

    private String country;

    private String userAgent;

    private String deviceType;

    // session tracking
    private String sessionId;

    // usage tracking
    private String action;  
    // preview, download, template_switch, payment_attempt

    private String templateId;

    // timestamp
    private LocalDateTime createdAt;

    public AppUsageLog() {
        this.createdAt = LocalDateTime.now();
    }

    // ---------------- getters/setters ----------------

    public Long getId() {
        return id;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}