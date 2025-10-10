package co.edu.uniquindio.application.dtos.reserva;

import co.edu.uniquindio.application.models.enums.ReservaEstado;

import java.time.LocalDate;

public record ItemReservaDTO(
        Long id,
        Long alojamientoId,
        String tituloAlojamiento,
        String ciudad,
        LocalDate fechaEntrada,
        LocalDate fechaSalida,
        Double precio,
        ReservaEstado estado
) {
}