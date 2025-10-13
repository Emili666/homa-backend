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

import poo.uniquindio.edu.co.homa.dto.request.RegistroUsuarioDTO;

@SpringBootTest
@AutoConfigureMockMvc
class UsuarioControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Nested
    @DisplayName("Escenarios de registro de usuario")
    class Registro {

        @Test
        @DisplayName("Registro exitoso (201)")
        void registroExitoso() throws Exception {
            RegistroUsuarioDTO req = RegistroUsuarioDTO.builder()
                    .nombre("Carlos Test")
                    .email("carlos_test@example.com")
                    .contrasena("Segura123!")
                    .build();

            var json = objectMapper.writeValueAsString(req);
            mockMvc.perform(post("/api/usuarios/registro")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
                    .andExpect(status().isCreated());
        }

        @Test
        @DisplayName("Email duplicado (400)")
        void emailDuplicado() throws Exception {
            RegistroUsuarioDTO req = RegistroUsuarioDTO.builder()
                    .nombre("Carlos")
                    .email("existe@example.com")
                    .contrasena("Segura123!")
                    .build();
            var json = objectMapper.writeValueAsString(req);
            mockMvc.perform(post("/api/usuarios/registro")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Contraseña insegura (400)")
        void contrasenaInsegura() throws Exception {
            RegistroUsuarioDTO req = RegistroUsuarioDTO.builder()
                    .nombre("Carlos")
                    .email("nuevo@example.com")
                    .contrasena("123")
                    .build();
            var json = objectMapper.writeValueAsString(req);
            mockMvc.perform(post("/api/usuarios/registro")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Email vacío (400)")
        void emailVacio() throws Exception {
            RegistroUsuarioDTO req = RegistroUsuarioDTO.builder()
                    .nombre("Carlos")
                    .email("")
                    .contrasena("Segura123!")
                    .build();
            var json = objectMapper.writeValueAsString(req);
            mockMvc.perform(post("/api/usuarios/registro")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
                    .andExpect(status().isBadRequest());
        }
    }
        
}

