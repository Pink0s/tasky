package com.tasky.api.configurations.securities;

import com.tasky.api.filters.JwtAuthenticationFilter;
import jakarta.servlet.DispatcherType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authorization.AuthorityAuthorizationManager;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.AuthorizationManagers;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;

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
        http.cors(withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((authorize) ->
                     authorize

                             .dispatcherTypeMatchers(DispatcherType.FORWARD, DispatcherType.ERROR)
                             .permitAll()
                             .requestMatchers(HttpMethod.GET,"/actuator/health").permitAll()
                             .requestMatchers(HttpMethod.POST,"/api/v1/user/auth")
                             .permitAll()
                             .requestMatchers("/api/v1/user/**")
                             .hasRole("ADMIN")
                             .requestMatchers(HttpMethod.GET,"/api/v1/project/**")
                             .access(
                                     AuthorizationManagers.allOf(
                                             AuthorityAuthorizationManager.hasAnyRole("USER","PROJECT_MANAGER")
                                     )
                             )
                             .requestMatchers("/api/v1/project/**")
                             .hasRole("PROJECT_MANAGER")
                             .requestMatchers("/api/v1/comment/**")
                             .access(
                                     AuthorizationManagers.allOf(
                                             AuthorityAuthorizationManager.hasAnyRole("USER","PROJECT_MANAGER")
                                     )
                             )
                             .requestMatchers("/api/v1/feature/**")
                             .access(
                                     AuthorizationManagers.allOf(
                                             AuthorityAuthorizationManager.hasAnyRole("USER","PROJECT_MANAGER")
                                     )
                             )
                             .requestMatchers("/api/v1/toDo/**")
                             .access(
                                     AuthorizationManagers.allOf(
                                             AuthorityAuthorizationManager.hasAnyRole("USER","PROJECT_MANAGER")
                                     )
                             )
                             .requestMatchers(HttpMethod.GET,"/api/v1/run/**")
                             .access(
                                     AuthorizationManagers.allOf(
                                             AuthorityAuthorizationManager.hasAnyRole("USER","PROJECT_MANAGER")
                                     )
                             )
                             .requestMatchers("/api/v1/run/**")
                             .hasRole("PROJECT_MANAGER")
                             .anyRequest()
                             .denyAll()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .sessionManagement((session) ->
                        session
                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider)
                .exceptionHandling((exceptionHandling) ->
                        exceptionHandling
                                .authenticationEntryPoint(authenticationEntryPoint)
                );

                return http.build();
    }
}
