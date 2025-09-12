package poo.uniquindio.edu.co.Homa.controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import poo.uniquindio.edu.co.Homa.dto.AuthRequestDto;
import poo.uniquindio.edu.co.Homa.dto.AuthResponseDto;
import poo.uniquindio.edu.co.Homa.dto.UsuarioDto;
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticación", description = "Controlador de autenticación con JWT")
public class AuthController {
     @Operation(summary = "Iniciar sesión", description = "Autentica al usuario y devuelve un token JWT")
    @PostMapping("/login")
    public AuthResponseDto login(@RequestBody AuthRequestDto request) {
        // Implementar autenticación real
        return new AuthResponseDto("fake-jwt-token");
    }
    @Operation(summary = "Registrar usuario", description = "Permite crear un nuevo usuario con credenciales")
    @PostMapping("/register")
    public UsuarioDto register(@RequestBody UsuarioDto dto) {
        return dto;
    }
}

