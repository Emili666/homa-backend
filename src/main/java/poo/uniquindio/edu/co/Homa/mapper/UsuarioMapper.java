package co.edu.uniquindio.homa.mapper;

import co.edu.uniquindio.homa.dto.request.UsuarioRegistroRequest;
import co.edu.uniquindio.homa.dto.response.UsuarioResponse;
import co.edu.uniquindio.homa.model.entity.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

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
    void updateEntityFromRequest(co.edu.uniquindio.homa.dto.request.ActualizarUsuarioRequest request, @MappingTarget Usuario usuario);
}
