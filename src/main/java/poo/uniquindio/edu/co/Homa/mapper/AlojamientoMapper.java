package poo.uniquindio.edu.co.Homa.mapper;
import poo.uniquindio.edu.co.Homa.dto.AlojamientoDto;
import poo.uniquindio.edu.co.Homa.model.Alojamiento;


public class AlojamientoMapper {

    public static AlojamientoDto toDto(Alojamiento alojamiento) {
        return new AlojamientoDto(
                alojamiento.getId() != null ? alojamiento.getId().toString() : null, // Convertimos Long → String
                alojamiento.getNombre(),
                alojamiento.getDescripcion(),
                alojamiento.getDireccion(),
                alojamiento.getPrecioPorNoche(),
                alojamiento.getCapacidad()
        );
    }

    public static Alojamiento toEntity(AlojamientoDto dto) {
        Alojamiento alojamiento = new Alojamiento(); // Constructor vacío para JPA
        alojamiento.setId(dto.id() != null ? Long.parseLong(dto.id()) : null); // Convertimos String → Long
        alojamiento.setNombre(dto.nombre());
        alojamiento.setDescripcion(dto.descripcion());
        alojamiento.setDireccion(dto.direccion());
        alojamiento.setPrecioPorNoche(dto.precioPorNoche());
        alojamiento.setCapacidad(dto.capacidad());
        return alojamiento;
    }
}