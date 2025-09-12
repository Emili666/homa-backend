package poo.uniquindio.edu.co.Homa.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@NotAllArguments
@AllArgsConstructor
public class Alojamiento {
    private String id;

    private String nombre;
    private String descripcion;
    private String direccion;
    private Double precioPorNoche;
    private Integer capacidad;
    
}
