package poo.uniquindio.edu.co.Homa.ControllerTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import poo.uniquindio.edu.co.Homa.dto.request.CrearAlojamientoDTO;



@SpringBootTest
@AutoConfigureMockMvc
class AlojamientoControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Nested
    @DisplayName("Escenarios de creación de alojamiento")
    class CrearAlojamiento {

        @Test
        @DisplayName("Creación exitosa (201)")
        void creacionExitosa() throws Exception {
            CrearAlojamientoDTO dto = CrearAlojamientoDTO.builder()
                    .titulo("Casa Bonita")
                    .ciudad("Armenia")
                    .direccion("Calle 123 #45-67") 
                    .precioPorNoche(120000f)  
                    .maxHuespedes(4)
                    .imagenes(List.of("imagen1.jpg"))
                    .build();

            var json = objectMapper.writeValueAsString(dto);
            mockMvc.perform(post("/api/alojamientos")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
                    .andExpect(status().isCreated());
        }

        @Test
        @DisplayName("Precio negativo (400)")
        void precioNegativo() throws Exception {
            CrearAlojamientoDTO dto = CrearAlojamientoDTO.builder()
                    .titulo("Casa Bonita")
                    .ciudad("Armenia")
                    .direccion("Calle 123 #45-67") 
                    .precioPorNoche(-120000f)  
                    .maxHuespedes(4)
                    .imagenes(List.of("imagen1.jpg"))
                    .build();

            var json = objectMapper.writeValueAsString(dto);
            mockMvc.perform(post("/api/alojamientos")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Capacidad inválida (400)")
        void capacidadInvalida() throws Exception {
            CrearAlojamientoDTO dto = CrearAlojamientoDTO.builder()
                    .titulo("Casa Bonita")
                    .ciudad("Armenia")
                    .direccion("Calle 123 #45-67") 
                    .precioPorNoche(120000f)  
                    .maxHuespedes(0)
                    .imagenes(List.of("imagen1.jpg"))
                    .build();

            var json = objectMapper.writeValueAsString(dto);
            mockMvc.perform(post("/api/alojamientos")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Ciudad vacía (400)")
        void ciudadVacia() throws Exception {
            CrearAlojamientoDTO dto = CrearAlojamientoDTO.builder()
                    .titulo("Casa Bonita")
                    .ciudad("")
                    .direccion("Calle 123 #45-67") 
                    .precioPorNoche(120000f)  
                    .maxHuespedes(4)
                    .imagenes(List.of("imagen1.jpg"))
                    .build();

            var json = objectMapper.writeValueAsString(dto);
            mockMvc.perform(post("/api/alojamientos")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
                    .andExpect(status().isBadRequest());
        }
    }
    
}
