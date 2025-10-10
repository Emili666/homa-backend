package poo.uniquindio.edu.co.homa.dto.response;

import co.edu.uniquindio.homa.model.enums.EstadoAlojamiento;
import co.edu.uniquindio.homa.model.enums.Servicio;
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
public class AlojamientoResponse {
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
    private List<String> imagenes;
    private List<Servicio> servicios;
    private String anfitrionId;
    private String nombreAnfitrion;
    private String fotoAnfitrion;
    private Double calificacionPromedio;
    private Integer totalResenas;
    private LocalDateTime creadoEn;
}
