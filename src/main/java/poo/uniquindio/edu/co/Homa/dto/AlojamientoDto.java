package poo.uniquindio.edu.co.Homa.dto;

public record AlojamientoDto(
        Long id,
        String nombre,
        String descripcion,
        String direccion,
        Double precioPorNoche,
        Integer capacidad
) {}
