package poo.uniquindio.edu.co.Homa.mapper;


import poo.uniquindio.edu.co.Homa.dto.ReservaDto;
import poo.uniquindio.edu.co.Homa.model.Reserva;

public class ReservaMapper {

    public static ReservaDto toDto(Reserva reserva) {
        return new ReservaDto(
                reserva.getId() != null ? reserva.getId().toString() : null,
                reserva.getUsuario() != null ? reserva.getUsuario().getId().toString() : null,
                reserva.getAlojamiento() != null ? reserva.getAlojamiento().getId().toString() : null,
                reserva.getFechaInicio() != null ? reserva.getFechaInicio().toString() : null,
                reserva.getFechaFin() != null ? reserva.getFechaFin().toString() : null,
                reserva.getTotal()
        );
    }

    public static Reserva toEntity(ReservaDto dto) {
        Reserva reserva = new Reserva();
        reserva.setId(dto.id() != null ? Long.valueOf(dto.id()) : null);
        // usuario y alojamiento se deben setear en el Service buscándolos por su ID
        reserva.setFechaInicio(dto.fechaInicio() != null ? java.time.LocalDate.parse(dto.fechaInicio()) : null);
        reserva.setFechaFin(dto.fechaFin() != null ? java.time.LocalDate.parse(dto.fechaFin()) : null);
        reserva.setTotal(dto.total());
        return reserva;
    }
}