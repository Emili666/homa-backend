package poo.uniquindio.edu.co.Homa.mapper;

import poo.uniquindio.edu.co.Homa.dto.UsuarioDto;
import poo.uniquindio.edu.co.Homa.model.Usuario;


public class UsuarioMapper {

    public static UsuarioDto toDto(Usuario usuario) {
        return new UsuarioDto(
                usuario.getId() != null ? usuario.getId().toString() : null,
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getTelefono()
        );
    }

    public static Usuario toEntity(UsuarioDto dto) {
        Usuario usuario = new Usuario();
        usuario.setId(dto.id() != null ? Long.valueOf(dto.id()) : null);
        usuario.setNombre(dto.nombre());
        usuario.setEmail(dto.email());
        usuario.setTelefono(dto.telefono());
        return usuario;
    }
}