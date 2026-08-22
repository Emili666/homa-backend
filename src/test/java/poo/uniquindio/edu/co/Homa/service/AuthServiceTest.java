package poo.uniquindio.edu.co.Homa.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import poo.uniquindio.edu.co.Homa.dto.request.LoginRequest;
import poo.uniquindio.edu.co.Homa.dto.response.LoginResponse;
import poo.uniquindio.edu.co.Homa.exception.UnauthorizedException;
import poo.uniquindio.edu.co.Homa.mapper.UsuarioMapper;
import poo.uniquindio.edu.co.Homa.model.entity.Usuario;
import poo.uniquindio.edu.co.Homa.model.enums.EstadoUsuario;
import poo.uniquindio.edu.co.Homa.model.enums.RolUsuario;
import poo.uniquindio.edu.co.Homa.repository.UsuarioRepository;
import poo.uniquindio.edu.co.Homa.security.JwtUtil;
import poo.uniquindio.edu.co.Homa.service.impl.AuthServiceImpl;
import poo.uniquindio.edu.co.Homa.dto.response.UsuarioResponse;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * TC-AUTH-001 a TC-AUTH-006
 * Pruebas unitarias del servicio de autenticación HOMA.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TC-AUTH — Pruebas unitarias del servicio de autenticación")
class AuthServiceTest {

    @Mock private AuthenticationManager authenticationManager;
    @Mock private JwtUtil jwtUtil;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private UsuarioMapper usuarioMapper;

    @InjectMocks
    private AuthServiceImpl authService;

    private Usuario usuarioActivo;
    private Usuario usuarioInactivo;

    @BeforeEach
    void setUp() {
        usuarioActivo = Usuario.builder()
                .id(1L)
                .nombre("Emili García")
                .email("emilibermudez6@gmail.com")
                .contrasena("$2a$10$hashedPassword")
                .rol(RolUsuario.Anfitrion)
                .estado(EstadoUsuario.ACTIVO)
                .codigoActivacion("cod123")
                .build();

        usuarioInactivo = Usuario.builder()
                .id(2L)
                .nombre("Usuario Inactivo")
                .email("inactivo@homa.com")
                .contrasena("$2a$10$hashedPassword")
                .rol(RolUsuario.Huesped)
                .estado(EstadoUsuario.INACTIVO)
                .codigoActivacion("cod456")
                .build();
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // TC-AUTH-001 — Login exitoso retorna JWT
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("TC-AUTH-001: Login con credenciales válidas → retorna token JWT")
    void login_conCredencialesValidas_retornaToken() {
        // Arrange
        LoginRequest request = new LoginRequest("emilibermudez6@gmail.com", "password123");
        UsuarioResponse usuarioResponse = UsuarioResponse.builder()
                .id(1L)
                .email("emilibermudez6@gmail.com")
                .build();

        when(usuarioRepository.findByEmail("emilibermudez6@gmail.com"))
                .thenReturn(Optional.of(usuarioActivo));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(jwtUtil.generarToken(anyString(), anyMap())).thenReturn("eyJhbGciOiJIUzI1NiJ9.token");
        when(usuarioMapper.toResponse(usuarioActivo)).thenReturn(usuarioResponse);

        // Act
        LoginResponse response = authService.login(request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getToken()).isNotBlank();
        assertThat(response.getTipo()).isEqualTo("Bearer");
        assertThat(response.getEmail()).isEqualTo("emilibermudez6@gmail.com");
        assertThat(response.getRol()).isEqualTo("ANFITRION");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // TC-AUTH-002 — Login con email inexistente
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("TC-AUTH-002: Login con email inexistente → lanza UnauthorizedException")
    void login_conEmailInexistente_lanzaUnauthorizedException() {
        // Arrange
        LoginRequest request = new LoginRequest("noexiste@fake.com", "cualquier");
        when(usuarioRepository.findByEmail("noexiste@fake.com")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("Credenciales inválidas");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // TC-AUTH-003 — Login con cuenta inactiva
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("TC-AUTH-003: Login con cuenta inactiva → lanza UnauthorizedException")
    void login_conCuentaInactiva_lanzaUnauthorizedException() {
        // Arrange
        LoginRequest request = new LoginRequest("inactivo@homa.com", "password123");
        when(usuarioRepository.findByEmail("inactivo@homa.com"))
                .thenReturn(Optional.of(usuarioInactivo));

        // Act & Assert
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("Cuenta no activa");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // TC-AUTH-004 — Login con contraseña incorrecta
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("TC-AUTH-004: Login con contraseña incorrecta → lanza excepción de autenticación")
    void login_conContrasenaIncorrecta_lanzaException() {
        // Arrange
        LoginRequest request = new LoginRequest("emilibermudez6@gmail.com", "wrongpassword");
        when(usuarioRepository.findByEmail("emilibermudez6@gmail.com"))
                .thenReturn(Optional.of(usuarioActivo));
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        // Act & Assert
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BadCredentialsException.class);
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // TC-AUTH-005 — Refresh token válido genera nuevo JWT
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("TC-AUTH-005: Refresh token válido → retorna nuevo JWT")
    void refreshToken_conTokenValido_retornaNuevoToken() {
        // Arrange
        String refreshToken = "valid.refresh.token";
        UsuarioResponse usuarioResponse = UsuarioResponse.builder()
                .id(1L)
                .email("emilibermudez6@gmail.com")
                .build();

        when(jwtUtil.validateToken(refreshToken)).thenReturn(true);
        when(jwtUtil.getEmailFromToken(refreshToken)).thenReturn("emilibermudez6@gmail.com");
        when(usuarioRepository.findByEmail("emilibermudez6@gmail.com"))
                .thenReturn(Optional.of(usuarioActivo));
        when(jwtUtil.generarToken(anyString(), anyMap())).thenReturn("nuevo.token.jwt");
        when(usuarioMapper.toResponse(usuarioActivo)).thenReturn(usuarioResponse);

        // Act
        LoginResponse response = authService.refreshToken(refreshToken);

        // Assert
        assertThat(response.getToken()).isEqualTo("nuevo.token.jwt");
        assertThat(response.getTipo()).isEqualTo("Bearer");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // TC-AUTH-006 — Refresh token inválido
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("TC-AUTH-006: Refresh token inválido → lanza UnauthorizedException")
    void refreshToken_conTokenInvalido_lanzaUnauthorizedException() {
        // Arrange
        when(jwtUtil.validateToken("token.invalido")).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> authService.refreshToken("token.invalido"))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("inválido");
    }
}
