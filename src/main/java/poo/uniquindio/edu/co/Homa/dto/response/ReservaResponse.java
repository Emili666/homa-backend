package poo.uniquindio.edu.co.Homa.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import poo.uniquindio.edu.co.Homa.model.enums.EstadoReserva;

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
    private String emailHuesped;
    private String telefonoHuesped;
    private Integer cantidadHuespedes;
    private LocalDate fechaEntrada;
    private LocalDate fechaSalida;
    private Double precio;
    private EstadoReserva estado;
    private LocalDateTime creadoEn;
    private LocalDateTime fechaCreacion; // Alias para creadoEn
}
