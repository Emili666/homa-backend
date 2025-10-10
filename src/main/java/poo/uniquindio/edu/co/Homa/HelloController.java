package poo.uniquindio.edu.co.homa;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api")
@Tag(name = "Ejemplo", description = "Endpoints de ejemplo para probar la API")
public class HelloController {

    @Operation(
        summary = "Saludo de bienvenida",
        description = "Devuelve un mensaje de bienvenida de la API"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Mensaje de bienvenida obtenido correctamente"
    )
    @GetMapping("/hello")
    public String hello() {
        return "Hola desde Spring Boot 🚀";
    }
}
