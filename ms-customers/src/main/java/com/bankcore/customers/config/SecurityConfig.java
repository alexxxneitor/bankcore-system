package com.bankcore.customers.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


/**
 * Security configuration class responsible for defining authentication and authorization
 * rules for the application.
 * <p>
 * This configuration:
 * <ul>
 *     <li>Disables CSRF protection (commonly used in stateless REST APIs).</li>
 *     <li>Allows unauthenticated access to authentication-related endpoints.</li>
 *     <li>Requires authentication for all other endpoints.</li>
 *     <li>Provides a {@link PasswordEncoder} bean for secure password hashing.</li>
 * </ul>
 * </p>
 */
@Configuration
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {

    /**
     * Configures the HTTP security filter chain.
     * <p>
     * Defines authorization rules and security settings for incoming HTTP requests.
     * </p>
     *
     * @param http the {@link HttpSecurity} object used to configure web-based security
     * @return the configured {@link SecurityFilterChain}
     * @throws Exception if an error occurs while building the security configuration
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .anyRequest().authenticated()
                );

        return http.build();
    }

    /**
     * Provides a {@link PasswordEncoder} bean used to hash passwords securely.
     * <p>
     * This implementation uses {@link BCryptPasswordEncoder}, which applies
     * a strong hashing algorithm with built-in salt to protect against
     * brute-force and rainbow table attacks.
     * </p>
     *
     * @return a BCrypt-based password encoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
