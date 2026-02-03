package hu.agoston.contractmanager.security;

import hu.agoston.contractmanager.exception.ErrorResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import java.io.IOException;
import java.io.PrintWriter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth

                        // ===== ADMIN API =====
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // ===== CONTRACTS =====
                        .requestMatchers(HttpMethod.GET, "/api/contracts/**")
                        .hasAnyRole("USER", "ADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/contracts")
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.PUT, "/api/contracts/**")
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.DELETE, "/api/contracts/**")
                        .hasRole("ADMIN")

                        // ===== PARTNERS =====
                        .requestMatchers(HttpMethod.GET, "/api/partners/**")
                        .hasAnyRole("USER", "ADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/partners")
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.DELETE, "/api/partners/**")
                        .hasRole("ADMIN")

                        // ===== EVERYTHING ELSE =====
                        .anyRequest().authenticated()
                )

                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) ->
                                writeError(
                                        response,
                                        HttpServletResponse.SC_UNAUTHORIZED,
                                        "UNAUTHORIZED",
                                        "Authentication required"
                                )
                        )
                        .accessDeniedHandler((request, response, accessDeniedException) ->
                                writeError(
                                        response,
                                        HttpServletResponse.SC_FORBIDDEN,
                                        "FORBIDDEN",
                                        "You do not have permission to perform this action"
                                )
                        )
                )

                .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    private void writeError(HttpServletResponse response,
                            int status,
                            String error,
                            String message) {

        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ErrorResponse body = new ErrorResponse(error, message);

        try {
            PrintWriter writer = response.getWriter();
            writer.write("""
                {
                  "error": "%s",
                  "message": "%s",
                  "timestamp": "%s"
                }
                """.formatted(
                    body.getError(),
                    body.getMessage(),
                    body.getTimestamp()
            ));
            writer.flush();
        } catch (IOException e) {
            // response already committed – ignore
        }
    }
}
