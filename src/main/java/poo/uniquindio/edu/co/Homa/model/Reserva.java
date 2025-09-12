package poo.uniquindio.edu.co.Homa.model;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@Builder
@NotAllArguments
@AllArgsConstructor
public class Reserva {
    private String id;
    private Usuario usuario;       // 
    private Alojamiento alojamiento; // 
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Double total;  
}
