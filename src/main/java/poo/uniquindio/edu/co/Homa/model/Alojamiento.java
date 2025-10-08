package poo.uniquindio.edu.co.Homa.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa un alojamiento dentro del sistema Homa.
 * Un alojamiento tiene información básica, precio, capacidad y
 * puede estar relacionado con varias reservas.
 */
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

    private String titulo;

    private String descripcion;

    private String direccion; // guardada como texto

    private Double precioPorNoche;

    private Integer maxHuespedes;

    @OneToMany(mappedBy = "alojamiento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reserva> reservas = new ArrayList<>();

    
    @ElementCollection
    private List<String> imagenes = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "alojamiento_servicios",
            joinColumns = @JoinColumn(name = "alojamiento_id"),
            inverseJoinColumns = @JoinColumn(name = "servicio_id")
    )
    private List<Servicio> servicios = new ArrayList<>();

    private String nombreAnfitrion;
}