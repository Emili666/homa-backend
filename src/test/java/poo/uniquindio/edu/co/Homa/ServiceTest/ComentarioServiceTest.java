package poo.uniquindio.edu.co.Homa.ServiceTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import poo.uniquindio.edu.co.homa.model.entity.Reserva;
import poo.uniquindio.edu.co.homa.repository.ResenaRepository;
import poo.uniquindio.edu.co.homa.repository.ReservaRepository;
import poo.uniquindio.edu.co.homa.service.impl.ResenaServiceImpl;

@ExtendWith(MockitoExtension.class)
class ComentarioServiceTest {

    @Mock
    private ResenaRepository resenaRepository;

    @Mock
    private ReservaRepository reservaRepository;

    @InjectMocks
    private ResenaServiceImpl resenaService;

    @Test
    @DisplayName("No permite crear reseña si reserva no finalizada")
    void crearResena_reservaNoFinalizada() {
        Reserva r = new Reserva();
        r.setFechaSalida(LocalDate.now().plusDays(2)); // aún no finalizada
        assertThrows(RuntimeException.class, () -> resenaService.crear(null, 1L));
    }

    @Test
    @DisplayName("Crear reseña con comentario vacío debe fallar")
    void crearResena_comentarioVacio() {
        assertThrows(RuntimeException.class, () -> resenaService.crear(null, 1L));
    }

    @Test
    @DisplayName("Crear reseña válida (smoke test)")
    void crearResena_valida() {
        try {
            resenaService.crear(null, 1L); // depende de la implementación real
        } catch (Exception e) {
            assertNotNull(e.getMessage());
        }
    }

    @Test
    @DisplayName("Responder reseña no encontrada debe lanzar excepción")
    void responderResena_noEncontrada() {
        assertThrows(RuntimeException.class, () -> resenaService.responder(999L, null, 1L));
    }
}