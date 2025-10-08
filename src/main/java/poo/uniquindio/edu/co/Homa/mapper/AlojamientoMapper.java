package poo.uniquindio.edu.co.Homa.mapper;
import poo.uniquindio.edu.co.Homa.dto.AlojamientoDto;
import poo.uniquindio.edu.co.Homa.dto.DireccionDto;
import poo.uniquindio.edu.co.Homa.model.Alojamiento;
import poo.uniquindio.edu.co.Homa.dto.AlojamientoDto;
import poo.uniquindio.edu.co.Homa.model.Alojamiento;

/**
 * Mapper que convierte entre entidades Alojamiento y sus DTOs.
 * Se utiliza para evitar exponer directamente las entidades JPA
 * en las respuestas REST del backend.
 */
public class AlojamientoMapper {

    // Convierte una entidad Alojamiento a su DTO correspondiente
    public static AlojamientoDto toDto(Alojamiento alojamiento) {
        if (alojamiento == null) return null;

        return new AlojamientoDto(
                alojamiento.getId(),
                alojamiento.getTitulo(),
                alojamiento.getDescripcion(),
                alojamiento.getDireccion(),
                alojamiento.getPrecioPorNoche().floatValue(),
                alojamiento.getMaxHuespedes(),
                alojamiento.getServicios(),
                alojamiento.getImagenes(),
                alojamiento.getNombreAnfitrion()
        );
    }

    // Convierte un DTO a una entidad Alojamiento lista para persistencia
    public static Alojamiento toEntity(AlojamientoDto dto) {
        if (dto == null) return null;

        Alojamiento alojamiento = new Alojamiento();
        alojamiento.setId(dto.id());
        alojamiento.setTitulo(dto.titulo());
        alojamiento.setDescripcion(dto.descripcion());
        alojamiento.setDireccion(dto.direccion());
        alojamiento.setPrecioPorNoche(dto.precioPorNoche().doubleValue());
        alojamiento.setMaxHuespedes(dto.maxHuespedes());
        alojamiento.setServicios(dto.servicios());
        alojamiento.setImagenes(dto.imagenes());
        alojamiento.setNombreAnfitrion(dto.nombreAnfitrion());

        return alojamiento;
    }
}