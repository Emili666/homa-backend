package poo.uniquindio.edu.co.Homa.entity;
import poo.uniquindio.edu.co.Homa.enums.Estado;
import poo.uniquindio.edu.co.Homa.enums.Servicio;
import co.edu.uniquindio.application.models.vo.Direccion;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AlojamientoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(nullable = false,  length = 2000)
    private String descripcion;

    @Embedded
    @Column(nullable = false)
    private Direccion direccion;

    @Column(nullable = false,  length = 3)
    private Integer maxHuespedes;

    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime creadoEn;

    @Column(nullable = false,  length = 20)
    private Float precioPorNoche;

    @ElementCollection
    @Column(length = 500, nullable = false)
    private List<String> imagenes;

    @Enumerated(EnumType.STRING)
    private Estado estado;

    @ElementCollection
    private Set<Servicio> servicios;

    @ManyToOne
    @JoinColumn(name = "anfitrion_id", nullable = false)
    private UsuarioEntity anfitrion;

    @OneToMany(mappedBy = "alojamiento")
    private List<ReservaEntity> reservas;

    @OneToMany(mappedBy = "alojamiento")
    private List<ComentarioEntity> comentarios;

    private Double promedioCalificaciones;

    private Integer numeroCalificaciones;

}
