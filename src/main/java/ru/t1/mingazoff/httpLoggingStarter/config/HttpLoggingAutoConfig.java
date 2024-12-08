package ru.t1.mingazoff.httpLoggingStarter.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.t1.mingazoff.httpLoggingStarter.aspect.MainAspect;

@Configuration
@EnableConfigurationProperties(HttpLoggingConfigProperties.class)
public class HttpLoggingAutoConfig {


    private final HttpLoggingConfigProperties properties;

    public HttpLoggingAutoConfig(HttpLoggingConfigProperties properties) {
        this.properties = properties;
    }


    @Bean
    @ConditionalOnProperty(name = "http.logging.enabled", havingValue = "true", matchIfMissing = true)
    public MainAspect mainAspect() {
        if (properties == null) {
            throw new IllegalStateException("HttpLoggingConfigProperties is null");
        }
        return new MainAspect(properties);
    }
}
