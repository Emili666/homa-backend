package poo.uniquindio.edu.co.Homa.dto;


import java.util.List;

import poo.uniquindio.edu.co.Homa.model.Servicio;

public record AlojamientoDto(

        Long id,
        
        String titulo,

        String descripcion,

        String direccion,

        Float precioPorNoche,

        Integer maxHuespedes,

        List<Servicio> servicios,

        List<String> imagenes,

        String nombreAnfitrion
) {}

