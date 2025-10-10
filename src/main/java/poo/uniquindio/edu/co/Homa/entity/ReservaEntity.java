package poo.uniquindio.edu.co.Homa.entity;
import java.time.LocalDate;
import java.time.LocalDateTime;

import co.edu.uniquindio.application.models.enums.ReservaEstado;
import jakarta.persistence.*;
import lombok.*;
import main.java.poo.uniquindio.edu.co.Homa.entity.Alojamiento;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReservaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate fechaEntrada;

    @Column(nullable = false)
    private LocalDate fechaSalida;

    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime creadoEn;

    @Column(nullable = false, length = 3)
    private Integer cantidadHuespedes;

    @Column(nullable = false, length = 20)
    private double precio;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservaEstado estado;

    @ManyToOne
    @JoinColumn(name = "alojamiento_id", nullable = false)
    private Alojamiento alojamiento;

    @ManyToOne
    @JoinColumn(name = "huesped_id", nullable = false)
    private Usuario huesped;

}
