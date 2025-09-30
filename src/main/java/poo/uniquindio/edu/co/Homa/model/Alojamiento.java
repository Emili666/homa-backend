package poo.uniquindio.edu.co.Homa.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "alojamientos")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Alojamiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String descripcion;
    private String direccion;
    private Double precioPorNoche;
    private Integer capacidad;

    // Relación con reservas
    @OneToMany(mappedBy = "alojamiento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reserva> reservas = new ArrayList<>();
}

