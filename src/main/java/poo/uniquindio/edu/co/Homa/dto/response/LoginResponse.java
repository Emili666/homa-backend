package poo.uniquindio.edu.co.homa.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String refreshToken;
    private UsuarioResponse usuario;

    // Agrega estos campos si los usas en el builder
    private String tipo;
    private String email;
    private String nombre;
    private String rol;
}
// ...existing code...