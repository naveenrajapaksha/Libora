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
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .authorizeHttpRequests(auth -> auth

                        // =========================
                        // PUBLIC
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
                        // MEMBER MANAGEMENT
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
                        // EVERYTHING ELSE
                        // AUTHENTICATED USERS
                        // =========================

                        .anyRequest().authenticated()
                )


                // =========================
                // EXCEPTION HANDLING
                // =========================

                .exceptionHandling(exception -> exception

                        // 401 Unauthorized
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

                        // 403 Forbidden
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