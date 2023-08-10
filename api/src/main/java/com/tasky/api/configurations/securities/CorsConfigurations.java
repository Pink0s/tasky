package com.tasky.api.configurations.securities;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Configuration class for Cross-Origin Resource Sharing (CORS) settings.
 */
@Configuration
public class CorsConfigurations {
    @Value("#{'${server.allowed-origins}'.split(',')}")
    private List<String> allowedOrigins;
    @Value("#{'${server.allowed-methods}'.split(',')}")
    private List<String> allowedMethods;
    /**
     * Creates a CorsConfigurationSource bean to configure CORS settings.
     *
     * @return A CorsConfigurationSource instance with configured CORS settings.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOrigins(allowedOrigins);
        corsConfiguration.setAllowedMethods(allowedMethods);
        corsConfiguration.addAllowedHeader(HttpHeaders.AUTHORIZATION);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**",corsConfiguration);
        return source;
    }
}
