package poo.uniquindio.edu.co.homa.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import poo.uniquindio.edu.co.homa.model.enums.EstadoReserva;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservaDTO {
    private Long id;
    private Long alojamientoId;
    private String tituloAlojamiento;
    private String huespedId;
    private String nombreHuesped;
    private Integer cantidadHuespedes;
    private LocalDate fechaEntrada;
    private LocalDate fechaSalida;
    private Double precio;
    private EstadoReserva estado;
    private LocalDateTime creadoEn;
}
