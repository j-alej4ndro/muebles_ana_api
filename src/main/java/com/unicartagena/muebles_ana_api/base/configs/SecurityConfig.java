package com.unicartagena.muebles_ana_api.base.configs;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Públicas
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/api-docs/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                        // Catálogo de productos activos (fachada de tienda)
                        .requestMatchers(HttpMethod.GET, "/productos/activos").permitAll()

                        // Perfil propio: autenticado
                        .requestMatchers("/usuarios/me").authenticated()
                        // Gestión de usuarios y roles: solo ADMINISTRADOR
                        .requestMatchers("/usuarios/**").hasRole("ADMINISTRADOR")

                        // Gestión de productos: EMPLEADO o ADMINISTRADOR
                        .requestMatchers("/productos/**").hasAnyRole("EMPLEADO", "ADMINISTRADOR")
                        // Gestión de proveedores: EMPLEADO o ADMINISTRADOR
                        .requestMatchers("/proveedores/**").hasAnyRole("EMPLEADO", "ADMINISTRADOR")
                        // Compras a proveedor: EMPLEADO o ADMINISTRADOR
                        .requestMatchers("/pedidos-proveedor/**").hasAnyRole("EMPLEADO", "ADMINISTRADOR")
                        // Ventas: autenticado (reglas finas en servicio)
                        .requestMatchers("/ordenes-venta/**").authenticated()

                        // Cualquier otra petición requiere autenticación
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
