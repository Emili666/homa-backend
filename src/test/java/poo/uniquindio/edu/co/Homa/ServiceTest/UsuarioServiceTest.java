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

import poo.uniquindio.edu.co.homa.dto.request.UsuarioRegistroRequest;
import poo.uniquindio.edu.co.homa.model.entity.Usuario;
import poo.uniquindio.edu.co.homa.repository.UsuarioRepository;
import poo.uniquindio.edu.co.homa.service.impl.UsuarioServiceImpl;

class UsuarioServiceTest {

    @Mock
    UsuarioRepository usuarioRepository;

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

        when(usuarioRepository.findByEmail("nuevo@example.com")).thenReturn(Optional.empty());
        // repository save can be mocked if necessary
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
