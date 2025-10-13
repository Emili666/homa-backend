package poo.uniquindio.edu.co.Homa.ControllerTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import poo.uniquindio.edu.co.homa.dto.request.ResenaRequest;


@SpringBootTest
@AutoConfigureMockMvc
class ResenaControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Nested
    @DisplayName("Escenarios de creación de reseñas")
    class CrearResena {

        @Test
        @DisplayName("Creación exitosa (201)")
        void creacionExitosa() throws Exception {
            ResenaRequest dto = new ResenaRequest();
            dto.setAlojamientoId(1L);
            dto.setCalificacion(5);
            dto.setComentario("Excelente alojamiento");

            var json = objectMapper.writeValueAsString(dto);
            mockMvc.perform(post("/api/resenas")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
                    .andExpect(status().isCreated());
        }

        @Test
        @DisplayName("Calificación fuera de rango (400)")
        void calificacionInvalida() throws Exception {
            ResenaRequest dto = new ResenaRequest();
            dto.setAlojamientoId(1L);
            dto.setCalificacion(7);
            dto.setComentario("Muy bueno");

            var json = objectMapper.writeValueAsString(dto);
            mockMvc.perform(post("/api/resenas")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Comentario vacío (400)")
        void comentarioVacio() throws Exception {
            ResenaRequest dto = new ResenaRequest();
            dto.setAlojamientoId(1L);
            dto.setCalificacion(4);
            dto.setComentario("");

            var json = objectMapper.writeValueAsString(dto);
            mockMvc.perform(post("/api/resenas")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Alojamiento inexistente (404)")
        void alojamientoNoExiste() throws Exception {
            ResenaRequest dto = new ResenaRequest();
            dto.setAlojamientoId(999L);
            dto.setCalificacion(4);
            dto.setComentario("Bonito lugar");

            var json = objectMapper.writeValueAsString(dto);
            mockMvc.perform(post("/api/resenas")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
                    .andExpect(status().isNotFound());
        }
    }
    
}
