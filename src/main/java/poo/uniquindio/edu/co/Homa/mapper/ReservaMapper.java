package poo.uniquindio.edu.co.Homa.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import poo.uniquindio.edu.co.Homa.dto.request.ReservaRequest;
import poo.uniquindio.edu.co.Homa.dto.response.ReservaResponse;
import poo.uniquindio.edu.co.Homa.model.entity.Reserva;

@Mapper(componentModel = "spring")
public interface ReservaMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "alojamiento", ignore = true)
    @Mapping(target = "huesped", ignore = true)
    @Mapping(target = "precio", ignore = true)
    @Mapping(target = "estado", ignore = true)
    @Mapping(target = "creadoEn", ignore = true)
    Reserva toEntity(ReservaRequest request);

    @Mapping(source = "alojamiento.id", target = "alojamientoId")
    @Mapping(source = "alojamiento.titulo", target = "tituloAlojamiento")
    @Mapping(source = "alojamiento.ciudad", target = "ciudadAlojamiento")
    @Mapping(source = "huesped.id", target = "huespedId")
    @Mapping(source = "huesped.nombre", target = "nombreHuesped")
    @Mapping(source = "huesped.email", target = "emailHuesped")
    @Mapping(source = "huesped.telefono", target = "telefonoHuesped")
    @Mapping(source = "creadoEn", target = "fechaCreacion")
    ReservaResponse toResponse(Reserva reserva);
}
