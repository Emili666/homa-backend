package co.edu.uniquindio.Homa.dto.response;

import co.edu.uniquindio.homa.model.enums.EstadoUsuario;
import co.edu.uniquindio.homa.model.enums.RolUsuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDTO {
    private String id;
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
