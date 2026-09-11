package com.libora.backend.config;

import com.libora.backend.security.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter
    ) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http

                // =========================
                // CSRF
                // =========================

                .csrf(csrf -> csrf.disable())


                // =========================
                // SESSION
                // =========================

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )


                // =========================
                // AUTHORIZATION
                // =========================

                .authorizeHttpRequests(auth -> auth

                        // =========================
                        // PUBLIC ENDPOINTS
                        // =========================

                        .requestMatchers(
                                "/api/auth/**"
                        ).permitAll()

                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**"
                        ).permitAll()

                        .requestMatchers(
                                "/api/health"
                        ).permitAll()


                        // =========================
                        // ADMIN
                        // ADMIN ONLY
                        // =========================

                        .requestMatchers(
                                "/api/admin/**"
                        ).hasRole("ADMIN")


                        // =========================
                        // LIBRARIAN DASHBOARD
                        // LIBRARIAN ONLY
                        // =========================

                        .requestMatchers(
                                "/api/librarian/**"
                        ).hasRole("LIBRARIAN")


                        // =========================
                        // MEMBER DASHBOARD
                        // MEMBER ONLY
                        // =========================

                        .requestMatchers(
                                "/api/member/**"
                        ).hasRole("MEMBER")


                        // =========================
                        // USER / MEMBER MANAGEMENT
                        // ADMIN + LIBRARIAN
                        // =========================

                        .requestMatchers(
                                "/api/users/**"
                        ).hasAnyRole(
                                "ADMIN",
                                "LIBRARIAN"
                        )


                        // =========================
                        // LIBRARIAN MANAGEMENT
                        // ADMIN ONLY
                        // =========================

                        .requestMatchers(
                                "/api/librarians/**"
                        ).hasRole("ADMIN")


                        // =========================
                        // BOOK MANAGEMENT
                        // =========================

                        // Create Book
                        // ADMIN + LIBRARIAN

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/books/**"
                        ).hasAnyRole(
                                "ADMIN",
                                "LIBRARIAN"
                        )


                        // Update Book
                        // ADMIN + LIBRARIAN

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/books/**"
                        ).hasAnyRole(
                                "ADMIN",
                                "LIBRARIAN"
                        )


                        // Delete Book
                        // ADMIN + LIBRARIAN

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/books/**"
                        ).hasAnyRole(
                                "ADMIN",
                                "LIBRARIAN"
                        )


                        // View Books
                        // ADMIN + LIBRARIAN + MEMBER

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/books/**"
                        ).authenticated()


                        // =========================
                        // TRANSACTIONS
                        // =========================

                        // All transaction endpoints
                        // require authentication

                        .requestMatchers(
                                "/api/transactions/**"
                        ).authenticated()


                        // =========================
                        // NOTIFICATIONS
                        // =========================

                        .requestMatchers(
                                "/api/notifications/**"
                        ).authenticated()


                        // =========================
                        // EVERYTHING ELSE
                        // =========================

                        .anyRequest().authenticated()
                )


                // =========================
                // EXCEPTION HANDLING
                // =========================

                .exceptionHandling(exception -> exception

                        // =========================
                        // 401 UNAUTHORIZED
                        // =========================

                        .authenticationEntryPoint(
                                (request, response, authException) -> {

                                    response.setStatus(
                                            HttpServletResponse.SC_UNAUTHORIZED
                                    );

                                    response.setContentType(
                                            "application/json"
                                    );

                                    response.getWriter().write(
                                            "{\"error\":\"Unauthorized\"}"
                                    );
                                }
                        )


                        // =========================
                        // 403 FORBIDDEN
                        // =========================

                        .accessDeniedHandler(
                                (request, response, accessDeniedException) -> {

                                    response.setStatus(
                                            HttpServletResponse.SC_FORBIDDEN
                                    );

                                    response.setContentType(
                                            "application/json"
                                    );

                                    response.getWriter().write(
                                            "{\"error\":\"Forbidden\"}"
                                    );
                                }
                        )
                )


                // =========================
                // JWT FILTER
                // =========================

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}