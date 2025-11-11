package poo.uniquindio.edu.co.homa.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResenaResponse {
    private Long id;
    private Long alojamientoId;
    private String tituloAlojamiento;
    private String usuarioId;
    private String nombreUsuario;
    private String fotoUsuario;
    private Integer calificacion;
    private String comentario;
    private String mensaje;
    private LocalDateTime respondidoEn;
    private LocalDateTime creadoEn;
}
