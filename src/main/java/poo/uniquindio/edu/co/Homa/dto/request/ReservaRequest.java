package poo.uniquindio.edu.co.homa.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservaRequest {

    @NotNull(message = "El ID del alojamiento es obligatorio")
    private Long alojamientoId;

    @NotNull(message = "La cantidad de huéspedes es obligatoria")
    @Min(value = 1, message = "Debe haber al menos 1 huésped")
    private Integer cantidadHuespedes;

    @NotNull(message = "La fecha de entrada es obligatoria")
    @FutureOrPresent(message = "La fecha de entrada debe ser hoy o en el futuro")
    private LocalDate fechaEntrada;

    @NotNull(message = "La fecha de salida es obligatoria")
    @Future(message = "La fecha de salida debe ser en el futuro")
    private LocalDate fechaSalida;
}
