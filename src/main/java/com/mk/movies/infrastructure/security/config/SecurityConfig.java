// src/main/java/com/mk/movies/infrastructure/security/config/SecurityConfig.java
package com.mk.movies.infrastructure.security.config;

import com.mk.movies.infrastructure.security.filters.JwtRequestFilter;
import com.mk.movies.infrastructure.security.service.JwtService;
import com.mk.movies.infrastructure.security.service.UserDetailsServiceImpl;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private static final String[] WHITE_LIST = {
        "/v2/api-docs",
        "/v3/api-docs",
        "/v3/api-docs/**",
        "/swagger-resources",
        "/swagger-resources/**",
        "/configuration/ui",
        "/configuration/security",
        "/swagger-ui/**",
        "/webjars/**",
        "/swagger-ui.html",
    };

    private final AuthenticationProvider authenticationProvider;

    @Bean
    public JwtRequestFilter jwtRequestFilter(UserDetailsServiceImpl userDetailsService, JwtService jwtService) {
        return new JwtRequestFilter(userDetailsService, jwtService);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtRequestFilter jwtRequestFilter) throws Exception {
        http
            .cors(cors -> corsConfigurationSource())
            .csrf(AbstractHttpConfigurer::disable)
            .securityMatcher("/api/v1/**")
            .authorizeHttpRequests(authRequests ->
                authRequests
                    .requestMatchers(WHITE_LIST).permitAll()
                    .requestMatchers(HttpMethod.POST,"/api/v1/auth", "/api/v1/users").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/v1/movies/**").permitAll()
                    .anyRequest().authenticated()
            )
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        var config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}