package poo.uniquindio.edu.co.homa.dto.response;

import co.edu.uniquindio.homa.model.enums.EstadoReserva;
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
public class ReservaResponse {
    private Long id;
    private Long alojamientoId;
    private String tituloAlojamiento;
    private String ciudadAlojamiento;
    private String huespedId;
    private String nombreHuesped;
    private Integer cantidadHuespedes;
    private LocalDate fechaEntrada;
    private LocalDate fechaSalida;
    private Double precio;
    private EstadoReserva estado;
    private LocalDateTime creadoEn;
}
