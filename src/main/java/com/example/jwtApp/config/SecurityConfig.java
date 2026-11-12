package com.example.jwtApp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    // 1. Password Encoder
    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }

    // 2. Authentication Provider
    @Bean
    public DaoAuthenticationProvider authenticationProvider(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(userDetailsService);

        provider.setPasswordEncoder(passwordEncoder);

        return provider;
    }

    // 3. Authentication Manager
    @Bean
    public AuthenticationManager authenticationManager(
            DaoAuthenticationProvider authenticationProvider) {

        return new ProviderManager(authenticationProvider);
    }

    // 4. JWT Authentication Converter
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {

        JwtGrantedAuthoritiesConverter authoritiesConverter =
                new JwtGrantedAuthoritiesConverter();

        authoritiesConverter.setAuthoritiesClaimName("authorities");

        // JWT must contain values such as ROLE_USER or ROLE_ADMIN
        authoritiesConverter.setAuthorityPrefix("");

        JwtAuthenticationConverter converter =
                new JwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(
                authoritiesConverter
        );

        return converter;
    }

    // 5. Bearer Token Resolver
    @Bean
    public BearerTokenResolver bearerTokenResolver() {

        DefaultBearerTokenResolver defaultResolver =
                new DefaultBearerTokenResolver();

        return request -> {

            // First check Authorization header
            String token = defaultResolver.resolve(request);

            if (token != null) {
                return token;
            }

            // Then check JWT cookie
            if (request.getCookies() != null) {

                for (var cookie : request.getCookies()) {

                    if ("JWT_TOKEN".equals(cookie.getName())
                            && cookie.getValue() != null
                            && !cookie.getValue().isBlank()) {

                        return cookie.getValue();
                    }
                }
            }

            return null;
        };
    }

    // 6. Security Filter Chain
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            DaoAuthenticationProvider authenticationProvider,
            BearerTokenResolver bearerTokenResolver,
            JwtAuthenticationConverter jwtAuthenticationConverter)
            throws Exception {

        http

            // Authentication Provider
            .authenticationProvider(authenticationProvider)

            // Session Management
            .sessionManagement(session ->
                    session.sessionCreationPolicy(
                            SessionCreationPolicy.STATELESS
                    )
            )

            // Authorization Rules
            .authorizeHttpRequests(auth -> auth

                    .requestMatchers(
                    		  "/",
                    	        "/login",
                    	        "/register",
                    	        "/css/**",
                    	        "/js/**",
                    	        "/images/**"
                    ).permitAll()

                    .requestMatchers("/api/auth/**").permitAll()

                    .requestMatchers("/api/admin/**")
                    .hasRole("ADMIN")

                    .anyRequest().authenticated()
            )

            // JWT Authentication
            .oauth2ResourceServer(oauth2 -> oauth2

                    .bearerTokenResolver(bearerTokenResolver)

                    .jwt(jwt -> jwt

                            .jwtAuthenticationConverter(
                                    jwtAuthenticationConverter
                            )
                    )
            );

        return http.build();
    }
}