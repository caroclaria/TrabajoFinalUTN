package com.utn.tpfinal.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(Customizer.withDefaults())
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Se evalúan en orden: gana la primera regla que coincida
                .requestMatchers("/error").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()

                .requestMatchers("/api/usuarios/**").hasRole("ADMINISTRADOR")

                .requestMatchers(HttpMethod.GET, "/api/nutricionistas/**")
                        .hasAnyRole("ADMINISTRADOR", "NUTRICIONISTA")
                .requestMatchers("/api/nutricionistas/**").hasRole("ADMINISTRADOR")

                .requestMatchers(HttpMethod.GET, "/api/camas/**").authenticated()
                .requestMatchers(HttpMethod.PATCH, "/api/camas/*/estado")
                        .hasAnyRole("ADMINISTRADOR", "NUTRICIONISTA")
                .requestMatchers(HttpMethod.POST, "/api/camas/*/asignar", "/api/camas/*/desasignar")
                        .hasAnyRole("ADMINISTRADOR", "NUTRICIONISTA")
                .requestMatchers("/api/camas/**").hasRole("ADMINISTRADOR")

                // Historia clínica: solo un NUTRICIONISTA puede firmar evoluciones (necesita matrícula);
                // el administrador puede consultarlas. Cocina no accede a datos clínicos.
                .requestMatchers(HttpMethod.POST, "/api/pacientes/*/evoluciones").hasRole("NUTRICIONISTA")
                .requestMatchers("/api/pacientes/**").hasAnyRole("ADMINISTRADOR", "NUTRICIONISTA")

                // Todo lo demás (los módulos de Carolina y Jazmín) exige estar logueado
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
            );

        return http.build();
    }

    // Convierte el claim "rol" del token (ej: "ADMINISTRADOR") en el permiso ROLE_ADMINISTRADOR
    private JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter authorities = new JwtGrantedAuthoritiesConverter();
        authorities.setAuthoritiesClaimName("rol");
        authorities.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(authorities);
        return converter;
    }

    // Permite que el frontend (otro puerto) llame a la API desde el navegador
    @Bean
    public CorsConfigurationSource corsConfigurationSource(
            @Value("${app.cors.allowed-origins}") String origins) {
        CorsConfiguration config = new CorsConfiguration();
        // trim(): tolera espacios después de las comas en CORS_ORIGINS
        config.setAllowedOrigins(Arrays.stream(origins.split(",")).map(String::trim).toList());
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}