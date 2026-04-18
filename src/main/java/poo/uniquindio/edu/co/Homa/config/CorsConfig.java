package poo.uniquindio.edu.co.Homa.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

        @Value("${app.cors.allowed-origins:http://localhost:4200}")
        private String[] allowedOrigins;

        // Origen de producción siempre permitido
        private static final String CLOUDFRONT_ORIGIN = "https://d3duuewq1nioxx.cloudfront.net";

        private java.util.List<String> getAllowedOrigins() {
                java.util.List<String> origins = new java.util.ArrayList<>(Arrays.asList(allowedOrigins));
                if (!origins.contains(CLOUDFRONT_ORIGIN)) {
                        origins.add(CLOUDFRONT_ORIGIN);
                }
                return origins;
        }

        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration configuration = new CorsConfiguration();

                // Orígenes permitidos (Angular frontend)
                configuration.setAllowedOrigins(getAllowedOrigins());

                // Métodos HTTP permitidos
                configuration.setAllowedMethods(Arrays.asList(
                                "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));

                // Headers permitidos
                configuration.setAllowedHeaders(Arrays.asList(
                                "Authorization",
                                "Content-Type",
                                "Accept",
                                "X-Requested-With",
                                "Cache-Control"));

                // Headers expuestos
                configuration.setExposedHeaders(Arrays.asList(
                                "Authorization",
                                "Content-Disposition"));

                // Permitir credenciales
                configuration.setAllowCredentials(true);

                // Tiempo de cache para preflight requests
                configuration.setMaxAge(3600L);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);

                return source;
        }

        @Override
        public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                                .allowedOrigins(getAllowedOrigins().toArray(new String[0]))
                                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                                .allowedHeaders("*")
                                .exposedHeaders("Authorization", "Content-Disposition")
                                .allowCredentials(true)
                                .maxAge(3600);
        }

}
