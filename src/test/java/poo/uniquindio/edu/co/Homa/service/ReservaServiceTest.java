package poo.uniquindio.edu.co.Homa.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import poo.uniquindio.edu.co.Homa.config.MetricsConfig.HomaBusinessMetrics;
import poo.uniquindio.edu.co.Homa.dto.request.ReservaRequest;
import poo.uniquindio.edu.co.Homa.dto.response.ReservaResponse;
import poo.uniquindio.edu.co.Homa.exception.BusinessException;
import poo.uniquindio.edu.co.Homa.exception.ResourceNotFoundException;
import poo.uniquindio.edu.co.Homa.mapper.ReservaMapper;
import poo.uniquindio.edu.co.Homa.model.entity.Alojamiento;
import poo.uniquindio.edu.co.Homa.model.entity.Reserva;
import poo.uniquindio.edu.co.Homa.model.entity.Usuario;
import poo.uniquindio.edu.co.Homa.model.enums.EstadoReserva;
import poo.uniquindio.edu.co.Homa.model.enums.RolUsuario;
import poo.uniquindio.edu.co.Homa.model.enums.EstadoUsuario;
import poo.uniquindio.edu.co.Homa.repository.AlojamientoRepository;
import poo.uniquindio.edu.co.Homa.repository.ReservaRepository;
import poo.uniquindio.edu.co.Homa.repository.UsuarioRepository;
import poo.uniquindio.edu.co.Homa.service.impl.ReservaServiceImpl;
import poo.uniquindio.edu.co.Homa.util.EmailService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * TC-RES-001 a TC-RES-015
 * Pruebas unitarias del servicio de reservas HOMA.
 * Flujo: PENDIENTE → CONFIRMADA / RECHAZADA → COMPLETADA / CANCELADA
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TC-RES — Pruebas unitarias del servicio de reservas")
class ReservaServiceTest {

    @Mock private ReservaRepository reservaRepository;
    @Mock private AlojamientoRepository alojamientoRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private ReservaMapper reservaMapper;
    @Mock private EmailService emailService;
    @Mock private HomaBusinessMetrics metrics;

    @InjectMocks
    private ReservaServiceImpl reservaService;

    private Usuario huesped;
    private Usuario anfitrion;
    private Alojamiento alojamiento;
    private Reserva reserva;

    @BeforeEach
    void setUp() {
        anfitrion = Usuario.builder()
                .id(1L).nombre("Anfitrion Test").email("anfitrion@homa.com")
                .rol(RolUsuario.Anfitrion).estado(EstadoUsuario.ACTIVO)
                .codigoActivacion("cod123").build();

        huesped = Usuario.builder()
                .id(2L).nombre("Huesped Test").email("huesped@homa.com")
                .rol(RolUsuario.Huesped).estado(EstadoUsuario.ACTIVO)
                .codigoActivacion("cod456").build();

        alojamiento = Alojamiento.builder()
                .id(10L).titulo("Cabaña en el Eje Cafetero").ciudad("Armenia")
                .precioPorNoche(100000.0).maxHuespedes(4).anfitrion(anfitrion).build();

        reserva = Reserva.builder()
                .id(100L).huesped(huesped).alojamiento(alojamiento)
                .fechaEntrada(LocalDate.now().plusDays(10))
                .fechaSalida(LocalDate.now().plusDays(13))
                .cantidadHuespedes(2).precio(300000.0)
                .estado(EstadoReserva.PENDIENTE)
                .creadoEn(LocalDateTime.now()).build();
    }

    @Test
    @DisplayName("TC-RES-001: Crear reserva con fechas disponibles → estado PENDIENTE")
    void crearReserva_conFechasDisponibles_retornaReservaEnPendiente() {
        ReservaRequest request = ReservaRequest.builder()
                .alojamientoId(10L).fechaEntrada(LocalDate.now().plusDays(10))
                .fechaSalida(LocalDate.now().plusDays(13)).cantidadHuespedes(2).build();

        ReservaResponse responseEsperado = ReservaResponse.builder()
                .id(100L).estado(EstadoReserva.PENDIENTE).precio(300000.0).build();

        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(huesped));
        when(alojamientoRepository.findById(10L)).thenReturn(Optional.of(alojamiento));
        when(reservaRepository.findReservasConflictivas(anyLong(), any(), any()))
                .thenReturn(Collections.emptyList());
        when(reservaMapper.toEntity(request)).thenReturn(reserva);
        when(reservaRepository.save(any(Reserva.class))).thenReturn(reserva);
        when(reservaMapper.toResponse(reserva)).thenReturn(responseEsperado);

        ReservaResponse resultado = reservaService.crear(request, 2L);

        assertThat(resultado.getEstado()).isEqualTo(EstadoReserva.PENDIENTE);
        assertThat(resultado.getPrecio()).isEqualTo(300000.0);
        verify(reservaRepository).save(any(Reserva.class));
        verify(metrics).incrementReservaCreada();
    }

    @Test
    @DisplayName("TC-RES-002: Crear reserva con fechas ocupadas → lanza BusinessException")
    void crearReserva_conFechasOcupadas_lanzaBusinessException() {
        ReservaRequest request = ReservaRequest.builder()
                .alojamientoId(10L).fechaEntrada(LocalDate.now().plusDays(10))
                .fechaSalida(LocalDate.now().plusDays(13)).cantidadHuespedes(2).build();

        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(huesped));
        when(alojamientoRepository.findById(10L)).thenReturn(Optional.of(alojamiento));
        when(reservaRepository.findReservasConflictivas(anyLong(), any(), any()))
                .thenReturn(Collections.singletonList(reserva));

        assertThatThrownBy(() -> reservaService.crear(request, 2L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("no está disponible");
        verify(reservaRepository, never()).save(any());
    }

    @Test
    @DisplayName("TC-RES-003: Fecha salida anterior a entrada → lanza BusinessException")
    void crearReserva_conFechasSalida_anteriorEntrada_lanzaException() {
        ReservaRequest request = ReservaRequest.builder()
                .alojamientoId(10L).fechaEntrada(LocalDate.now().plusDays(13))
                .fechaSalida(LocalDate.now().plusDays(10)).cantidadHuespedes(2).build();

        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(huesped));
        when(alojamientoRepository.findById(10L)).thenReturn(Optional.of(alojamiento));

        assertThatThrownBy(() -> reservaService.crear(request, 2L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("posterior a la fecha de entrada");
    }

    @Test
    @DisplayName("TC-RES-004: Huéspedes exceden capacidad máxima → lanza BusinessException")
    void crearReserva_excediendoCapacidad_lanzaException() {
        ReservaRequest request = ReservaRequest.builder()
                .alojamientoId(10L).fechaEntrada(LocalDate.now().plusDays(10))
                .fechaSalida(LocalDate.now().plusDays(13)).cantidadHuespedes(10).build();

        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(huesped));
        when(alojamientoRepository.findById(10L)).thenReturn(Optional.of(alojamiento));

        assertThatThrownBy(() -> reservaService.crear(request, 2L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("excede la capacidad");
    }

    @Test
    @DisplayName("TC-RES-005: Anfitrión confirma reserva PENDIENTE → estado CONFIRMADA")
    void confirmarReserva_enEstadoPendiente_cambiaAConfirmada() {
        reserva.setEstado(EstadoReserva.PENDIENTE);
        when(reservaRepository.findById(100L)).thenReturn(Optional.of(reserva));
        when(reservaRepository.save(any(Reserva.class))).thenReturn(reserva);

        reservaService.confirmarReserva(100L, 1L);

        assertThat(reserva.getEstado()).isEqualTo(EstadoReserva.CONFIRMADA);
        verify(metrics).incrementReservaConfirmada();
    }

    @Test
    @DisplayName("TC-RES-006: Anfitrión rechaza reserva PENDIENTE → estado RECHAZADA")
    void rechazarReserva_enEstadoPendiente_cambiaARechazada() {
        reserva.setEstado(EstadoReserva.PENDIENTE);
        when(reservaRepository.findById(100L)).thenReturn(Optional.of(reserva));
        when(reservaRepository.save(any(Reserva.class))).thenReturn(reserva);

        reservaService.rechazarReserva(100L, 1L);

        assertThat(reserva.getEstado()).isEqualTo(EstadoReserva.RECHAZADA);
        verify(metrics).incrementReservaRechazada();
    }

    @Test
    @DisplayName("TC-RES-007: Confirmar reserva ya CONFIRMADA → lanza BusinessException")
    void confirmarReserva_yaConfirmada_lanzaException() {
        reserva.setEstado(EstadoReserva.CONFIRMADA);
        when(reservaRepository.findById(100L)).thenReturn(Optional.of(reserva));

        assertThatThrownBy(() -> reservaService.confirmarReserva(100L, 1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Solo se pueden confirmar reservas pendientes");
        verify(reservaRepository, never()).save(any());
    }

    @Test
    @DisplayName("TC-RES-008: Rechazar reserva ya RECHAZADA → lanza BusinessException")
    void rechazarReserva_yaRechazada_lanzaException() {
        reserva.setEstado(EstadoReserva.RECHAZADA);
        when(reservaRepository.findById(100L)).thenReturn(Optional.of(reserva));

        assertThatThrownBy(() -> reservaService.rechazarReserva(100L, 1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Solo se pueden rechazar reservas pendientes");
    }

    @Test
    @DisplayName("TC-RES-009: Anfitrión completa reserva CONFIRMADA → estado COMPLETADA")
    void completarReserva_enEstadoConfirmada_cambiaACompletada() {
        reserva.setEstado(EstadoReserva.CONFIRMADA);
        when(reservaRepository.findById(100L)).thenReturn(Optional.of(reserva));
        when(reservaRepository.save(any(Reserva.class))).thenReturn(reserva);

        reservaService.completarReserva(100L, 1L);

        assertThat(reserva.getEstado()).isEqualTo(EstadoReserva.COMPLETADA);
        verify(metrics).incrementReservaCompletada();
    }

    @Test
    @DisplayName("TC-RES-010: Completar reserva PENDIENTE → lanza BusinessException")
    void completarReserva_enEstadoPendiente_lanzaException() {
        reserva.setEstado(EstadoReserva.PENDIENTE);
        when(reservaRepository.findById(100L)).thenReturn(Optional.of(reserva));

        assertThatThrownBy(() -> reservaService.completarReserva(100L, 1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Solo se pueden completar reservas confirmadas");
    }

    @Test
    @DisplayName("TC-RES-011: Huésped cancela con más de 48h → estado CANCELADA")
    void cancelarReserva_conMas48Horas_cambiaACancelada() {
        reserva.setEstado(EstadoReserva.PENDIENTE);
        reserva.setFechaEntrada(LocalDate.now().plusDays(10));
        when(reservaRepository.findById(100L)).thenReturn(Optional.of(reserva));
        when(reservaRepository.save(any(Reserva.class))).thenReturn(reserva);

        reservaService.cancelar(100L, 2L);

        assertThat(reserva.getEstado()).isEqualTo(EstadoReserva.CANCELADA);
        verify(metrics).incrementReservaCancelada();
    }

    @Test
    @DisplayName("TC-RES-012: Cancelar con menos de 48h → lanza BusinessException")
    void cancelarReserva_conMenos48Horas_lanzaException() {
        reserva.setEstado(EstadoReserva.CONFIRMADA);
        reserva.setFechaEntrada(LocalDate.now().plusDays(1));
        when(reservaRepository.findById(100L)).thenReturn(Optional.of(reserva));

        assertThatThrownBy(() -> reservaService.cancelar(100L, 2L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("menos de 48 horas");
    }

    @Test
    @DisplayName("TC-RES-013: Huésped ajeno intenta cancelar → lanza BusinessException")
    void cancelarReserva_porHuespedAjeno_lanzaException() {
        reserva.setEstado(EstadoReserva.PENDIENTE);
        when(reservaRepository.findById(100L)).thenReturn(Optional.of(reserva));

        assertThatThrownBy(() -> reservaService.cancelar(100L, 99L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("No tienes permiso");
    }

    @Test
    @DisplayName("TC-RES-014: Anfitrión ajeno intenta confirmar → lanza BusinessException")
    void confirmarReserva_porAnfitrionAjeno_lanzaException() {
        reserva.setEstado(EstadoReserva.PENDIENTE);
        when(reservaRepository.findById(100L)).thenReturn(Optional.of(reserva));

        assertThatThrownBy(() -> reservaService.confirmarReserva(100L, 99L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("No tienes permiso");
    }

    @Test
    @DisplayName("TC-RES-015: Confirmar reserva inexistente → lanza ResourceNotFoundException")
    void confirmarReserva_inexistente_lanzaResourceNotFoundException() {
        when(reservaRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reservaService.confirmarReserva(999L, 1L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
