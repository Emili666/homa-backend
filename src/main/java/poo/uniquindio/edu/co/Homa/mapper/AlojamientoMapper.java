package poo.uniquindio.edu.co.Homa.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import poo.uniquindio.edu.co.Homa.dto.request.AlojamientoRequest;
import poo.uniquindio.edu.co.Homa.dto.response.AlojamientoResponse;
import poo.uniquindio.edu.co.Homa.model.entity.Alojamiento;

@Mapper(componentModel = "spring")
public interface AlojamientoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "estado", ignore = true)
    @Mapping(target = "anfitrion", ignore = true)
    @Mapping(target = "reservas", ignore = true)
    @Mapping(target = "resenas", ignore = true)
    @Mapping(target = "creadoEn", ignore = true)
    Alojamiento toEntity(AlojamientoRequest request);

    @Mapping(source = "anfitrion.id", target = "anfitrionId")
    @Mapping(source = "anfitrion.nombre", target = "nombreAnfitrion")
    @Mapping(source = "anfitrion.foto", target = "fotoAnfitrion")
    @Mapping(target = "calificacionPromedio", ignore = true)
    @Mapping(target = "totalResenas", ignore = true)
    @Mapping(target = "totalFavoritos", ignore = true)
    @Mapping(target = "esFavorito", ignore = true)
    AlojamientoResponse toResponse(Alojamiento alojamiento);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "anfitrion", ignore = true)
    @Mapping(target = "reservas", ignore = true)
    @Mapping(target = "resenas", ignore = true)
    @Mapping(target = "creadoEn", ignore = true)
    void updateEntityFromRequest(AlojamientoRequest request, @MappingTarget Alojamiento alojamiento);
}
