package poo.uniquindio.edu.co.Homa.ServiceTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import poo.uniquindio.edu.co.homa.dto.request.UsuarioRegistroRequest;
import poo.uniquindio.edu.co.homa.mapper.UsuarioMapper;
import poo.uniquindio.edu.co.homa.model.entity.Usuario;
import poo.uniquindio.edu.co.homa.repository.ContrasenaCodigoReinicioRepository;
import poo.uniquindio.edu.co.homa.repository.UsuarioRepository;
import poo.uniquindio.edu.co.homa.service.impl.UsuarioServiceImpl;
import poo.uniquindio.edu.co.homa.util.EmailService;

class UsuarioServiceTest {

    @Mock
    UsuarioRepository usuarioRepository;

    @Mock
    ContrasenaCodigoReinicioRepository codigoReinicioRepository;

    @Mock
    UsuarioMapper usuarioMapper;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    EmailService emailService;

    @InjectMocks
    UsuarioServiceImpl usuarioService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Registrar usuario con email duplicado debe lanzar excepción")
    void registrarUsuario_EmailDuplicado() {
        UsuarioRegistroRequest dto = UsuarioRegistroRequest.builder()
                .nombre("Juan")
                .email("exists@example.com")
                .contrasena("Segura123!")
                .build();

        Usuario usuarioExistente = new Usuario();
        usuarioExistente.setEmail("exists@example.com");

        when(usuarioRepository.findByEmail("exists@example.com"))
                .thenReturn(Optional.of(usuarioExistente));

        assertThrows(RuntimeException.class, () -> usuarioService.registrar(dto));
    }

    @Test
    @DisplayName("Contraseña insegura debe lanzar excepción")
    void registrarUsuario_ContrasenaInsegura() {
        UsuarioRegistroRequest dto = UsuarioRegistroRequest.builder()

                .nombre("Juan")
                .email("nuevo@example.com")
                .contrasena("123")
                .build();

        assertThrows(RuntimeException.class, () -> usuarioService.registrar(dto));
    }

    @Test
    @DisplayName("Registro exitoso (smoke)")
    void registrarUsuario_Exitoso() {
        UsuarioRegistroRequest dto = UsuarioRegistroRequest.builder()
                .nombre("Juan")
                .email("nuevo@example.com")
                .contrasena("Segura123!")
                .build();

        Usuario usuario = new Usuario();
        usuario.setEmail(dto.getEmail());

        when(usuarioRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(usuarioMapper.toEntity(dto)).thenReturn(usuario);
        when(passwordEncoder.encode("Segura123!")).thenReturn("hashed");
        when(usuarioRepository.save(usuario)).thenReturn(usuario);
        when(usuarioMapper.toResponse(usuario)).thenReturn(null);

        assertDoesNotThrow(() -> usuarioService.registrar(dto));
    }

    @Test
    @DisplayName("Generar código recuperación (smoke)")
    void generarCodigoRecuperacion() {

        try {
            usuarioService.solicitarRecuperacionContrasena(null);
        } catch (Exception e) {
            assertNotNull(e.getMessage());
        }
    }

}
