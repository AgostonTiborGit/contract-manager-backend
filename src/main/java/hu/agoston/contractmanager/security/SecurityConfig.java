package hu.agoston.contractmanager.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import hu.agoston.contractmanager.exception.ErrorResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import java.io.IOException;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/error").permitAll()

                        // ===== ADMIN API =====
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // ===== AUTH =====
                        .requestMatchers("/api/auth/**").authenticated()

                        // ===== CONTRACTS =====
                        .requestMatchers(HttpMethod.GET, "/api/contracts/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/contracts/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/contracts/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/contracts/**").hasRole("ADMIN")

                        // ===== PARTNERS =====
                        .requestMatchers(HttpMethod.GET, "/api/partners/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/partners").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/partners/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/partners/**").hasRole("ADMIN")

                        .anyRequest().authenticated()
                )

                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) ->
                                writeError(response, HttpServletResponse.SC_UNAUTHORIZED,
                                        "UNAUTHORIZED", "Authentication required")
                        )
                        .accessDeniedHandler((request, response, accessDeniedException) ->
                                writeError(response, HttpServletResponse.SC_FORBIDDEN,
                                        "FORBIDDEN", "You do not have permission")
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

        try {
            objectMapper.writeValue(response.getWriter(), new ErrorResponse(error, message));
        } catch (IOException ignored) {
            // response already committed – ignore
        }
    }
}