package ru.t1.mingazoff.httpLoggingStarter.config;


import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "http.logging")
public class HttpLoggingConfigProperties {

    private  String enabled;

    private  String level;

    public String getLevel() {
        return level;
    }

    public String getEnabled() {
        return enabled;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public void setEnabled(String enabled) {
        this.enabled = enabled;
    }

}
