package poo.uniquindio.edu.co.Homa.ControllerTest;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {
    /* 

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Nested
    @DisplayName("Escenarios de login")
    class LoginScenarios {

        @Test
        @DisplayName("Login exitoso (200)")
        void loginExitoso() throws Exception {
            LoginRequest req = new LoginRequest();
            req.setEmail("admin@example.com");
            req.setContrasena("Segura123!");
            var json = objectMapper.writeValueAsString(req);

            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").exists());
        }

        @Test
        @DisplayName("Credenciales inválidas (401)")
        void credencialesInvalidas() throws Exception {
            LoginRequest req = new LoginRequest();
            req.setEmail("noexiste@example.com");
            req.setContrasena("mala");
            var json = objectMapper.writeValueAsString(req);

            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Payload inválido (400)")
        void payloadInvalido() throws Exception {
            // missing password
            String json = "{\"email\":\"user@example.com\"}";
            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Usuario no existente (401)")
        void usuarioNoEncontrado() throws Exception {
            LoginRequest req = new LoginRequest();
            req.setEmail("inexistente@example.com");
            req.setContrasena("Segura123!");
            var json = objectMapper.writeValueAsString(req);

            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
                    .andExpect(status().isUnauthorized());
        }
    }
        */
}
