package poo.uniquindio.edu.co.Homa.mapper;

import poo.uniquindio.edu.co.Homa.dto.ReservaDto;
import poo.uniquindio.edu.co.Homa.model.Reserva;

public class ReservaMapper {

    public static ReservaDto ReservaDto(Reserva reserva) {
        return new ReservaDto(
                reserva.getId(),
                reserva.getUsuario().getId(),
                reserva.getAlojamiento().getId(),
                reserva.getFechaInicio().toString(),
                reserva.getFechaFin().toString(),
                reserva.getTotal()
        );
    }

    public static Reserva toEntity(ReservaDto dto) {
        Reserva reserva = new Reserva(null, null, null, null, null, null);
        reserva.setId(dto.id());
        // Ojo: usuario y alojamiento deben setearse en el Service buscando con su ID
        return reserva;
    }
}