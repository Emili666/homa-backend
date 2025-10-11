package poo.uniquindio.edu.co.homa.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import poo.uniquindio.edu.co.homa.dto.request.ActualizarUsuarioRequest;
import poo.uniquindio.edu.co.homa.dto.request.UsuarioRegistroRequest;
import poo.uniquindio.edu.co.homa.dto.response.UsuarioResponse;
import poo.uniquindio.edu.co.homa.model.entity.Usuario;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "estado", ignore = true)
    @Mapping(target = "esAnfitrion", ignore = true)
    @Mapping(target = "creadoEn", ignore = true)
    @Mapping(target = "perfilAnfitrion", ignore = true)
    @Mapping(target = "alojamientos", ignore = true)
    @Mapping(target = "reservas", ignore = true)
    @Mapping(target = "resenas", ignore = true)
    @Mapping(target = "codigosReinicio", ignore = true)
    Usuario toEntity(UsuarioRegistroRequest request);

    UsuarioResponse toResponse(Usuario usuario);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "contrasena", ignore = true)
    @Mapping(target = "rol", ignore = true)
    @Mapping(target = "estado", ignore = true)
    @Mapping(target = "esAnfitrion", ignore = true)
    @Mapping(target = "fechaNacimiento", ignore = true)
    @Mapping(target = "creadoEn", ignore = true)
    @Mapping(target = "perfilAnfitrion", ignore = true)
    @Mapping(target = "alojamientos", ignore = true)
    @Mapping(target = "reservas", ignore = true)
    @Mapping(target = "resenas", ignore = true)
    @Mapping(target = "codigosReinicio", ignore = true)
    void updateEntityFromRequest(ActualizarUsuarioRequest request, @MappingTarget Usuario usuario);
}
