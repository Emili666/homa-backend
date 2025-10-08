package poo.uniquindio.edu.co.Homa.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import poo.uniquindio.edu.co.Homa.security.JwtAuthenticationEntryPoint;
import poo.uniquindio.edu.co.Homa.security.JwtAuthenticationFilter;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 🔒 Desactiva CSRF porque trabajamos con JWT
                .csrf(AbstractHttpConfigurer::disable)

                // 🌍 Permitir peticiones desde el frontend (CORS)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 🚫 Sin sesiones, autenticación stateless con tokens
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 🧩 Definir qué endpoints son públicos y cuáles requieren autenticación
                .authorizeHttpRequests(req -> req
                        // Endpoints públicos
                        .requestMatchers(
                                "/api/auth/**",        // login, registro, refresh
                                "/swagger-ui/**",      // documentación Swagger
                                "/v3/api-docs/**",
                                "/api/imagenes/**"     // imágenes públicas
                        ).permitAll()
                        // Ejemplo: permitir GET a usuarios (opcional)
                        .requestMatchers(HttpMethod.GET, "/api/usuarios/**").permitAll()
                        // Todo lo demás requiere token JWT
                        .anyRequest().authenticated()
                )

                // ⚠️ Manejo de errores si el token no es válido o no hay permisos
                .exceptionHandling(ex -> ex.authenticationEntryPoint(new JwtAuthenticationEntryPoint()))

                // 🧱 Registrar el filtro JWT antes del filtro por defecto de Spring Security
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // Aquí colocas las URLs de tu frontend Angular
        config.setAllowedOrigins(List.of(
                "http://localhost:4200",
                "http://127.0.0.1:4200",
                "http://localhost:5500",
                "http://127.0.0.1:5500"
        ));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Encriptación segura con BCrypt
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
            throws Exception {
        // Permite autenticar usuarios en el AuthService
        return configuration.getAuthenticationManager();
    }
}