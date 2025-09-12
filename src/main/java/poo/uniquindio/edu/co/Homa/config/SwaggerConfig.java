package poo.uniquindio.edu.co.Homa.config;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
     @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API - Proyecto Homa UQ")
                        .version("1.0")
                        .description("Documentación de la API para gestión de usuarios, alojamientos y reservas"))
                .addServersItem(new Server().url("http://localhost:8080").description("Servidor local"));
    }
}

