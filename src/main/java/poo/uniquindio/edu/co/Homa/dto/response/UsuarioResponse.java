package poo.uniquindio.edu.co.Homa.dto.response;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import poo.uniquindio.edu.co.Homa.model.enums.EstadoUsuario;
import poo.uniquindio.edu.co.Homa.model.enums.RolUsuario;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponse {
    private Long id;
    private String nombre;
    private String email;
    private String telefono;
    private String foto;
    private LocalDate fechaNacimiento;
    private EstadoUsuario estado;
    private RolUsuario rol;
    private Boolean esAnfitrion;
    private LocalDateTime creadoEn;
}
