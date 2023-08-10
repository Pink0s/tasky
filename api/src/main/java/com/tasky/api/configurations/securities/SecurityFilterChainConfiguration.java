package com.tasky.api.configurations.securities;

import com.tasky.api.filters.JwtAuthenticationFilter;
import jakarta.servlet.DispatcherType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuration class for setting up the security filter chain.
 */
@Configuration
@EnableWebSecurity
public class SecurityFilterChainConfiguration {
    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationEntryPoint authenticationEntryPoint;

    /**
     * Constructs a SecurityFilterChainConfiguration with the specified components.
     *
     * @param authenticationProvider   The AuthenticationProvider implementation.
     * @param jwtAuthenticationFilter  The AuthenticationFilter implementation.
     * @param authenticationEntryPoint The AuthenticationEntryPoint implementation
     */
    public SecurityFilterChainConfiguration(AuthenticationProvider authenticationProvider, JwtAuthenticationFilter jwtAuthenticationFilter, AuthenticationEntryPoint authenticationEntryPoint) {
        this.authenticationProvider = authenticationProvider;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    /**
     * Creates and configures the security filter chain.
     *
     * @param http The HttpSecurity instance to configure.
     * @return A configured SecurityFilterChain instance.
     * @throws Exception If an exception occurs during configuration.
     */
    @Bean
    public SecurityFilterChain securityFilterChain (HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests((authorize) ->
                authorize
                        .dispatcherTypeMatchers(DispatcherType.FORWARD, DispatcherType.ERROR)
                        .permitAll()
                        .requestMatchers(HttpMethod.POST,"/api/v1/user/auth")
                        .permitAll()
                        .anyRequest()
                        .authenticated()
        ).sessionManagement((session) -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        ).authenticationProvider(authenticationProvider)
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling((exceptionHandling) ->
                        exceptionHandling
                                .authenticationEntryPoint(authenticationEntryPoint)
                )
                .build();
    }
}
