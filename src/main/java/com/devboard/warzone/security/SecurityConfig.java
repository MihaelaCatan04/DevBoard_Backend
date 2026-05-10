package com.devboard.warzone.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf(AbstractHttpConfigurer::disable)

                .cors(Customizer.withDefaults())

                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth

                        .requestMatchers("/token", "/error").permitAll().requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll().requestMatchers("/auth/register", "/auth/login").permitAll()

                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET, "/posts/**").permitAll().requestMatchers("/posts").permitAll()


                        .requestMatchers(HttpMethod.POST, "/posts/**").hasAnyRole("WRITER", "ADMIN").requestMatchers(HttpMethod.PUT, "/posts/**").hasAnyRole("WRITER", "ADMIN").requestMatchers(HttpMethod.DELETE, "/posts/**").hasAnyRole("WRITER", "ADMIN").requestMatchers(HttpMethod.POST, "/posts/*/vote").hasAnyRole("WRITER", "ADMIN").requestMatchers(HttpMethod.POST, "/posts/*/comments").hasAnyRole("WRITER", "ADMIN")

                        .anyRequest().authenticated())

                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}