package poo.uniquindio.edu.co.homa.dto.response;

import co.edu.uniquindio.homa.model.enums.EstadoAlojamiento;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlojamientoDTO {
    private Long id;
    private String titulo;
    private String descripcion;
    private String ciudad;
    private String direccion;
    private Float latitud;
    private Float longitud;
    private Float precioPorNoche;
    private Integer maxHuespedes;
    private EstadoAlojamiento estado;
    private String anfitrionId;
    private String nombreAnfitrion;
    private List<String> imagenes;
    private List<Integer> servicios;
    private Double promedioCalificacion;
    private LocalDateTime creadoEn;
}
