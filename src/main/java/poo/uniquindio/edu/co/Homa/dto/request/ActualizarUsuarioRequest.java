package poo.uniquindio.edu.co.homa.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActualizarUsuarioRequest {

    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    private String nombre;

    @Email(message = "El email debe ser válido")
    @Size(max = 100, message = "El email no puede exceder 100 caracteres")
    private String email;

    @Pattern(regexp = "^[0-9]{10,15}$", message = "El teléfono debe tener entre 10 y 15 dígitos")
    private String telefono;

    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String contrasena;

    private String foto;
}
