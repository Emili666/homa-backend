package poo.uniquindio.edu.co.homa.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import poo.uniquindio.edu.co.homa.dto.request.LoginRequest;
import poo.uniquindio.edu.co.homa.dto.response.LoginResponse;
import poo.uniquindio.edu.co.homa.service.AuthService;

@Tag(name = "Autenticación", description = "Endpoints para autenticación y autorización mediante JWT")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(
        summary = "Iniciar sesión",
        description = "Autentica un usuario y devuelve un token JWT si las credenciales son correctas."
    )
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @Operation(
        summary = "Cerrar sesión",
        description = "Invalida el token JWT actual, removiéndolo del almacenamiento temporal (si aplica)."
    )
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        String token = extraerToken(authHeader);
        if (token == null) {
            return ResponseEntity.badRequest().build();
        }
        authService.logout(token);
        return ResponseEntity.ok().build();
    }

    @Operation(
        summary = "Refrescar token",
        description = "Genera un nuevo token JWT a partir de un token de refresco válido."
    )
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refreshToken(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        String refreshToken = extraerToken(authHeader);
        if (refreshToken == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(authService.refreshToken(refreshToken));
    }

    /**
     * Extrae el token del header Authorization con prefijo 'Bearer '.
     */
    private String extraerToken(String header) {
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}
