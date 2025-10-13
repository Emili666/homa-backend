package poo.uniquindio.edu.co.Homa.ControllerTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import poo.uniquindio.edu.co.homa.dto.request.ReservaRequest;

@SpringBootTest
@AutoConfigureMockMvc
class ReservaControllerTest {
/* 
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Nested
    @DisplayName("Escenarios de creación de reservas")
    class CrearReserva {

        @Test
        @DisplayName("Reserva creada correctamente (201)")
        void reservaExitosa() throws Exception {
            ReservaRequest req = ReservaRequest.builder()
                    .alojamientoId(1L)
                    .cantidadHuespedes(2)
                    .fechaEntrada(LocalDate.now().plusDays(3))
                    .fechaSalida(LocalDate.now().plusDays(6))
                    .build();

            var json = objectMapper.writeValueAsString(req);
            mockMvc.perform(post("/api/reservas")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
                    .andExpect(status().isCreated());
        }

        @Test
        @DisplayName("Fechas invertidas (400)")
        void fechasInvertidas() throws Exception {
            ReservaRequest req = ReservaRequest.builder()
                    .alojamientoId(1L)
                    .cantidadHuespedes(2)
                    .fechaEntrada(LocalDate.now().plusDays(6))
                    .fechaSalida(LocalDate.now().plusDays(3))
                    .build();
            var json = objectMapper.writeValueAsString(req);

            mockMvc.perform(post("/api/reservas")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Fechas iguales (400)")
        void fechasIguales() throws Exception {
            ReservaRequest req = ReservaRequest.builder()
                    .alojamientoId(1L)
                    .cantidadHuespedes(2)
                    .fechaEntrada(LocalDate.now().plusDays(3))
                    .fechaSalida(LocalDate.now().plusDays(3))
                    .build();
            var json = objectMapper.writeValueAsString(req);

            mockMvc.perform(post("/api/reservas")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Alojamiento inexistente (404)")
        void alojamientoNoExiste() throws Exception {
            ReservaRequest req = ReservaRequest.builder()
                    .alojamientoId(999L)
                    .cantidadHuespedes(2)
                    .fechaEntrada(LocalDate.now().plusDays(3))
                    .fechaSalida(LocalDate.now().plusDays(6))
                    .build();
            var json = objectMapper.writeValueAsString(req);

            mockMvc.perform(post("/api/reservas")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
                    .andExpect(status().isNotFound());
        }

    }
        */
}
