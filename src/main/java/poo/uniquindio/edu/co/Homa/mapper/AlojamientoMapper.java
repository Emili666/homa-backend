package poo.uniquindio.edu.co.Homa.mapper;
import poo.uniquindio.edu.co.Homa.dto.AlojamientoDto;
import poo.uniquindio.edu.co.Homa.model.Alojamiento;

public class AlojamientoMapper {

    public static AlojamientoDto toDTO(Alojamiento alojamiento) {
        return new AlojamientoDto(
                alojamiento.getId(),
                alojamiento.getNombre(),
                alojamiento.getDescripcion(),
                alojamiento.getDireccion(),
                alojamiento.getPrecioPorNoche(),
                alojamiento.getCapacidad()
        );
    }

    public static Alojamiento toEntity(AlojamientoDto dto) {
        Alojamiento alojamiento = new Alojamiento(null, null, null, null, null, null);
        alojamiento.setId(dto.id());
        alojamiento.setNombre(dto.nombre());
        alojamiento.setDescripcion(dto.descripcion());
        alojamiento.setDireccion(dto.direccion());
        alojamiento.setPrecioPorNoche(dto.precioPorNoche());
        alojamiento.setCapacidad(dto.capacidad());
        return alojamiento;
    }
}
