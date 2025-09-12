package poo.uniquindio.edu.co.Homa.dto;


public record AlojamientoDto(
        String id,
        String nombre,
        String descripcion,
        String direccion,
        Double precioPorNoche,
        Integer capacidad
) {}
