package poo.uniquindio.edu.co.Homa.ServiceTest;


import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import poo.uniquindio.edu.co.homa.dto.request.ReservaRequest;
import poo.uniquindio.edu.co.homa.model.entity.Reserva;
import poo.uniquindio.edu.co.homa.repository.ReservaRepository;
import poo.uniquindio.edu.co.homa.service.impl.ReservaServiceImpl;

class ReservaServiceTest {
    /* 

    @Mock
    ReservaRepository reservaRepository;

    @InjectMocks
    ReservaServiceImpl reservaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("No debe permitir fechas invertidas")
    void crearReserva_FechasInvalidas() {
        ReservaRequest req = ReservaRequest.builder()
                .alojamientoId(1L)
                .cantidadHuespedes(2)
                .fechaEntrada(LocalDate.of(2025,5,10))
                .fechaSalida(LocalDate.of(2025,5,8))
                .build();

        assertThrows(RuntimeException.class, () -> reservaService.crear(req, 1L));
    }

    @Test
    @DisplayName("Debe exigir mínimo 1 noche")
    void crearReserva_MenosDeUnaNoche() {
        ReservaRequest req = ReservaRequest.builder()
                .alojamientoId(1L)
                .cantidadHuespedes(2)
                .fechaEntrada(LocalDate.now().plusDays(3))
                .fechaSalida(LocalDate.now().plusDays(3))
                .build();
        assertThrows(RuntimeException.class, () -> reservaService.crear(req, 1L));
    }

    @Test
    @DisplayName("Cancelar reserva con menos de 48h falla")
    void cancelarReserva_FueraDelLimite() {
        // si tu servicio tiene cancelar(id, clienteId) que verifica fecha inicio...
        // Mock repository to return reserva con fechaInicio muy cercano
        Reserva r = new Reserva();
        r.setFechaEntrada(LocalDate.now().plusDays(1)); // less than 48 hours
        when(reservaRepository.findById(1L)).thenReturn(java.util.Optional.of(r));
        assertThrows(RuntimeException.class, () -> reservaService.cancelar(1L, 1L));
    }

    @Test
    @DisplayName("Crear reserva válida (smoke)")
    void crearReserva_Valida() {
        ReservaRequest req = ReservaRequest.builder()
                .alojamientoId(1L)
                .cantidadHuespedes(2)
                .fechaEntrada(LocalDate.now().plusDays(5))
                .fechaSalida(LocalDate.now().plusDays(7))
                .build();
        when(reservaRepository.save(any(Reserva.class))).thenAnswer(inv -> inv.getArgument(0));
        assertDoesNotThrow(() -> reservaService.crear(req, 1L));
    }
        */
}
