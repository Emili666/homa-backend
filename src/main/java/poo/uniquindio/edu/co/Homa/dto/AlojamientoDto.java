package poo.uniquindio.edu.co.Homa.dto;


import java.util.List;

import poo.uniquindio.edu.co.Homa.model.Servicio;

public record AlojamientoDTO(

        Long id,
        
        String titulo,

        String descripcion,

        DireccionDTO direccion,

        Float precioPorNoche,

        Integer maxHuespedes,

        List<Servicio> servicios,

        List<String> imagenes,

        String nombreAnfitrion
) {
}
