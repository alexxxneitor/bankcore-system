package com.bankcore.customers.config;

import com.bankcore.customers.utils.enums.UserRole;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
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
 *     <li>Custom error handling for 401 and 403 responses.</li>
 *     <li>Provides a {@link PasswordEncoder} bean for secure password hashing.</li>
 * </ul>
 * </p>
 *
 * @author BankCore Team
 * @version 1.0
 */
@Configuration
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {

    /**
     * Configures the {@link SecurityFilterChain} to define the security behavior of the application.
     * <p>
     * It integrates custom handlers for authentication and authorization failures,
     * ensures the application is stateless, and secures endpoints based on user roles.
     * </p>
     *
     * @param http                           The {@link HttpSecurity} to modify.
     * @param customAuthenticationEntryPoint The handler for 401 Unauthorized errors.
     * @param customAccessDeniedHandler      The handler for 403 Forbidden errors.
     * @return The built {@link SecurityFilterChain}.
     * @throws Exception If an error occurs during the configuration of the security filters.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            CustomAuthenticationEntryPoint customAuthenticationEntryPoint,
            CustomAccessDeniedHandler customAccessDeniedHandler
    ) throws Exception {

        http
                .exceptionHandling(e ->
                        e.authenticationEntryPoint(customAuthenticationEntryPoint)
                                .accessDeniedHandler(customAccessDeniedHandler)
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/auth/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()
                        .requestMatchers("/api/customers/me").hasRole(UserRole.CUSTOMER.name())
                        .anyRequest().denyAll()
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
