package poo.uniquindio.edu.co.homa.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import poo.uniquindio.edu.co.homa.dto.request.ResenaRequest;
import poo.uniquindio.edu.co.homa.dto.response.ResenaResponse;
import poo.uniquindio.edu.co.homa.model.entity.Resena;

@Mapper(componentModel = "spring")
public interface ResenaMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "alojamiento", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "mensaje", ignore = true)
    @Mapping(target = "respondidoEn", ignore = true)
    @Mapping(target = "creadoEn", ignore = true)
    Resena toEntity(ResenaRequest request);

    @Mapping(source = "alojamiento.id", target = "alojamientoId")
    @Mapping(source = "usuario.id", target = "usuarioId")
    @Mapping(source = "usuario.nombre", target = "nombreUsuario")
    @Mapping(source = "usuario.foto", target = "fotoUsuario")
    ResenaResponse toResponse(Resena resena);
}
