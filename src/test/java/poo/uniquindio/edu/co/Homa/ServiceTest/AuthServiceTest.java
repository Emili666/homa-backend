package poo.uniquindio.edu.co.Homa.ServiceTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;

import poo.uniquindio.edu.co.Homa.dto.request.LoginRequest;
import poo.uniquindio.edu.co.Homa.exception.UnauthorizedException;
import poo.uniquindio.edu.co.Homa.model.entity.Usuario;
import poo.uniquindio.edu.co.Homa.model.enums.EstadoUsuario;
import poo.uniquindio.edu.co.Homa.model.enums.RolUsuario;
import poo.uniquindio.edu.co.Homa.repository.UsuarioRepository;
import poo.uniquindio.edu.co.Homa.security.JwtUtil;
import poo.uniquindio.edu.co.Homa.service.impl.AuthServiceImpl;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    UsuarioRepository usuarioRepository;

    @Mock
    AuthenticationManager authenticationManager;

    @Mock
    JwtUtil jwtUtil;

    @InjectMocks
    AuthServiceImpl authService;

    @Test
    @DisplayName("Login con credenciales inválidas lanza excepción")
    void loginCredencialesInvalidas() {
        LoginRequest req = new LoginRequest();
        req.setEmail("noexiste@example.com");
        req.setContrasena("mala");

        when(usuarioRepository.findByEmail("noexiste@example.com"))
                .thenReturn(Optional.empty());

        assertThrows(UnauthorizedException.class, () -> authService.login(req));
    }

    @Test
    @DisplayName("Login exitoso genera token y no lanza excepción")
    void loginExitoso() {
        // Arrange
        LoginRequest req = new LoginRequest();
        req.setEmail("user@example.com");
        req.setContrasena("Segura123!");

        Usuario user = new Usuario();
        user.setEmail("user@example.com");
        user.setContrasena("Segura123!");
        user.setEstado(EstadoUsuario.ACTIVO);
        user.setRol(RolUsuario.Anfitrion);
        user.setNombre("Juan Ejemplo");

        // Simular búsqueda del usuario en base de datos
        when(usuarioRepository.findByEmail(eq("user@example.com"))).thenReturn(Optional.of(user));

        // Simular autenticación exitosa
        Authentication mockAuth = org.mockito.Mockito.mock(Authentication.class);
        when(authenticationManager.authenticate(any())).thenReturn(mockAuth);

        // Simular generación de token JWT
        when(jwtUtil.generarToken(anyString(), anyMap())).thenReturn("token-de-prueba");

        // Act & Assert
        assertDoesNotThrow(() -> authService.login(req));
    }

    @Test
    @DisplayName("Login con usuario inactivo lanza excepción")
    void loginUsuarioInactivo() {
        LoginRequest req = new LoginRequest();
        req.setEmail("user@example.com");
        req.setContrasena("123");

        Usuario usuario = new Usuario();
        usuario.setEmail("user@example.com");
        usuario.setEstado(EstadoUsuario.INACTIVO);

        when(usuarioRepository.findByEmail("user@example.com"))
                .thenReturn(Optional.of(usuario));

        assertThrows(UnauthorizedException.class, () -> authService.login(req));
    }

    @Test
    @DisplayName("Refresh token inválido lanza excepción")
    void refreshInvalido() {
        when(jwtUtil.validateToken("token-malo")).thenReturn(false);
        assertThrows(UnauthorizedException.class, () -> authService.refreshToken("token-malo"));
    }
}