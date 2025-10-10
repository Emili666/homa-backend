package co.edu.uniquindio.application.dtos.reserva;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CreacionReservaDTO(
        @NotNull
        Long alojamientoId,
        Long usuarioId,
        @NotNull @FutureOrPresent
        LocalDate fechaEntrada,
        @NotNull @FutureOrPresent
        LocalDate fechaSalida,
        @NotNull @Min(1)
        Integer numeroHuespedes
) {
}