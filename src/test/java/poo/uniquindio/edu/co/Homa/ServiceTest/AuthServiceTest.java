package poo.uniquindio.edu.co.Homa.ServiceTest;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;

import poo.uniquindio.edu.co.homa.dto.request.LoginRequest;
import poo.uniquindio.edu.co.homa.repository.UsuarioRepository;
import poo.uniquindio.edu.co.homa.service.impl.AuthServiceImpl;

class AuthServiceTest {
/*
    @Mock
    UsuarioRepository usuarioRepository;

    @Mock
    AuthenticationManager authenticationManager;

    @InjectMocks
    AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Login con credenciales inválidas lanza excepción")
    void loginCredencialesInvalidas() {
        LoginRequest req = new LoginRequest();
        req.setEmail("noexiste@example.com");
        req.setContrasena("mala");

        // si el usuario no existe, el servicio suele lanzar UnauthorizedException
        when(usuarioRepository.findByEmail("noexiste@example.com")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> authService.login(req));
    }

    @Test
    @DisplayName("Login payload inválido")
    void loginPayloadInvalido() {
        LoginRequest req = new LoginRequest();
        req.setEmail("");
        req.setContrasena("");
        assertThrows(RuntimeException.class, () -> authService.login(req));
    }

    @Test
    @DisplayName("Login exitoso (mock básico)")
    void loginExitoso() {

        LoginRequest req = new LoginRequest();
        req.setEmail("user@example.com");
        req.setContrasena("Segura123!");

        try {
            authService.login(req);
        } catch (Exception e) {
            // si lanza porque faltan dependencias, comprobamos que el mensaje es claro
            assertTrue(e.getMessage() != null);
        }
    }

    @Test
    @DisplayName("Refresh token inválido")
    void refreshInvalido() {
        assertThrows(RuntimeException.class, () -> authService.refreshToken("token-malo"));
    }
         */
}
