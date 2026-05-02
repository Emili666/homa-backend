package poo.uniquindio.edu.co.Homa.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import poo.uniquindio.edu.co.Homa.dto.request.AlojamientoRequest;
import poo.uniquindio.edu.co.Homa.dto.response.AlojamientoResponse;
import poo.uniquindio.edu.co.Homa.exception.ResourceNotFoundException;
import poo.uniquindio.edu.co.Homa.exception.UnauthorizedException;
import poo.uniquindio.edu.co.Homa.mapper.AlojamientoMapper;
import poo.uniquindio.edu.co.Homa.model.entity.Alojamiento;
import poo.uniquindio.edu.co.Homa.model.entity.Usuario;
import poo.uniquindio.edu.co.Homa.model.enums.EstadoAlojamiento;
import poo.uniquindio.edu.co.Homa.model.enums.RolUsuario;
import poo.uniquindio.edu.co.Homa.model.enums.EstadoUsuario;
import poo.uniquindio.edu.co.Homa.repository.AlojamientoRepository;
import poo.uniquindio.edu.co.Homa.repository.FavoritoRepository;
import poo.uniquindio.edu.co.Homa.repository.UsuarioRepository;
import poo.uniquindio.edu.co.Homa.service.impl.AlojamientoServiceImpl;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * TC-ALOJ-001 a TC-ALOJ-006
 * Pruebas unitarias del servicio de alojamientos HOMA.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TC-ALOJ — Pruebas unitarias del servicio de alojamientos")
class AlojamientoServiceTest {

    @Mock private AlojamientoRepository alojamientoRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private AlojamientoMapper alojamientoMapper;
    @Mock private FavoritoRepository favoritoRepository;
    @Mock private ImageStorageService imageStorageService;

    @InjectMocks
    private AlojamientoServiceImpl alojamientoService;

    private Usuario anfitrion;
    private Usuario otroAnfitrion;
    private Alojamiento alojamiento;
    private AlojamientoRequest request;

    @BeforeEach
    void setUp() {
        anfitrion = Usuario.builder()
                .id(1L)
                .nombre("Anfitrion Test")
                .email("anfitrion@homa.com")
                .rol(RolUsuario.Anfitrion)
                .estado(EstadoUsuario.ACTIVO)
                .codigoActivacion("cod123")
                .build();

        otroAnfitrion = Usuario.builder()
                .id(99L)
                .nombre("Otro Anfitrion")
                .email("otro@homa.com")
                .rol(RolUsuario.Anfitrion)
                .estado(EstadoUsuario.ACTIVO)
                .codigoActivacion("cod999")
                .build();

        alojamiento = Alojamiento.builder()
                .id(10L)
                .titulo("Cabaña en el Eje Cafetero")
                .ciudad("Armenia")
                .precioPorNoche(100000.0)
                .maxHuespedes(4)
                .estado(EstadoAlojamiento.ACTIVO)
                .anfitrion(anfitrion)
                .imagenes(new ArrayList<>())
                .build();

        request = AlojamientoRequest.builder()
                .titulo("Cabaña en el Eje Cafetero")
                .descripcion("Hermosa cabaña con vista al paisaje cafetero, ideal para descanso familiar")
                .ciudad("Armenia")
                .direccion("Vereda El Caimo km 3")
                .precioPorNoche(100000.0)
                .maxHuespedes(4)
                .build();
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // TC-ALOJ-001 — Crear alojamiento exitosamente
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("TC-ALOJ-001: Anfitrión crea alojamiento → retorna respuesta con id")
    void crearAlojamiento_conDatosValidos_retornaAlojamientoCreado() {
        // Arrange
        AlojamientoResponse responseEsperado = AlojamientoResponse.builder()
                .id(10L)
                .titulo("Cabaña en el Eje Cafetero")
                .ciudad("Armenia")
                .estado(EstadoAlojamiento.ACTIVO)
                .build();

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(anfitrion));
        when(alojamientoMapper.toEntity(request)).thenReturn(alojamiento);
        when(alojamientoRepository.save(any(Alojamiento.class))).thenReturn(alojamiento);
        when(alojamientoMapper.toResponse(alojamiento)).thenReturn(responseEsperado);
        when(favoritoRepository.countByAlojamientoId(10L)).thenReturn(0L);

        // Act
        AlojamientoResponse resultado = alojamientoService.crear(request, 1L);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(10L);
        assertThat(resultado.getCiudad()).isEqualTo("Armenia");
        verify(alojamientoRepository).save(any(Alojamiento.class));
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // TC-ALOJ-002 — Crear alojamiento con anfitrión inexistente
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("TC-ALOJ-002: Crear alojamiento con anfitrión inexistente → lanza ResourceNotFoundException")
    void crearAlojamiento_anfitrionInexistente_lanzaException() {
        // Arrange
        when(usuarioRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> alojamientoService.crear(request, 999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Anfitrión no encontrado");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // TC-ALOJ-003 — Obtener alojamiento por ID existente
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("TC-ALOJ-003: Obtener alojamiento por ID existente → retorna datos correctos")
    void obtenerPorId_existente_retornaAlojamiento() {
        // Arrange
        AlojamientoResponse responseEsperado = AlojamientoResponse.builder()
                .id(10L)
                .titulo("Cabaña en el Eje Cafetero")
                .build();

        when(alojamientoRepository.findById(10L)).thenReturn(Optional.of(alojamiento));
        when(alojamientoMapper.toResponse(alojamiento)).thenReturn(responseEsperado);
        when(favoritoRepository.countByAlojamientoId(10L)).thenReturn(5L);

        // Act
        AlojamientoResponse resultado = alojamientoService.obtenerPorId(10L);

        // Assert
        assertThat(resultado.getId()).isEqualTo(10L);
        assertThat(resultado.getTitulo()).isEqualTo("Cabaña en el Eje Cafetero");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // TC-ALOJ-004 — Obtener alojamiento inexistente
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("TC-ALOJ-004: Obtener alojamiento con ID inexistente → lanza ResourceNotFoundException")
    void obtenerPorId_inexistente_lanzaException() {
        // Arrange
        when(alojamientoRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> alojamientoService.obtenerPorId(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Alojamiento no encontrado");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // TC-ALOJ-005 — Anfitrión ajeno no puede actualizar
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("TC-ALOJ-005: Anfitrión ajeno intenta actualizar → lanza UnauthorizedException")
    void actualizarAlojamiento_porAnfitrionAjeno_lanzaException() {
        // Arrange
        when(alojamientoRepository.findById(10L)).thenReturn(Optional.of(alojamiento));

        // Act & Assert — otroAnfitrion.id=99L, propietario es 1L
        assertThatThrownBy(() -> alojamientoService.actualizar(10L, request, 99L))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("No tienes permiso");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // TC-ALOJ-006 — Eliminar alojamiento con reservas futuras
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("TC-ALOJ-006: Eliminar alojamiento con reservas futuras → lanza RuntimeException")
    void eliminarAlojamiento_conReservasFuturas_lanzaException() {
        // Arrange
        when(alojamientoRepository.findById(10L)).thenReturn(Optional.of(alojamiento));
        when(alojamientoRepository.tieneReservasFuturas(10L)).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> alojamientoService.eliminar(10L, 1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("reservas futuras");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // TC-ALOJ-007 — Cambiar estado de alojamiento
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("TC-ALOJ-007: Admin cambia estado de alojamiento a PENDIENTE")
    void cambiarEstado_aPendiente_guardaCorrectamente() {
        // Arrange
        when(alojamientoRepository.findById(10L)).thenReturn(Optional.of(alojamiento));
        when(alojamientoRepository.save(any(Alojamiento.class))).thenReturn(alojamiento);

        // Act
        alojamientoService.cambiarEstado(10L, EstadoAlojamiento.PENDIENTE);

        // Assert
        assertThat(alojamiento.getEstado()).isEqualTo(EstadoAlojamiento.PENDIENTE);
        verify(alojamientoRepository).save(alojamiento);
    }
}
