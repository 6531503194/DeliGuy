package com.deliguy.auth_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**", "/actuator/**", "/h2-console/**", "/swagger-ui/**",
                        "/v3/api-docs/**", "swagger-ui.html", "/auth-service/**")
                .permitAll()
                .anyRequest().denyAll()
            )
            .headers(headers -> headers.frameOptions(frame -> frame.disable()));
            // .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
