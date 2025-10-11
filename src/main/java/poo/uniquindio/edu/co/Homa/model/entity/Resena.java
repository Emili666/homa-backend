package poo.uniquindio.edu.co.homa.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "resena", indexes = {
    @Index(name = "idx_alojamiento_id", columnList = "alojamiento_id"),
    @Index(name = "idx_usuario_id", columnList = "usuario_id"),
    @Index(name = "idx_calificacion", columnList = "calificacion")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Resena {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alojamiento_id", nullable = false)
    private Alojamiento alojamiento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false)
    private Integer calificacion;

    @Column(length = 2000)
    private String comentario;

    @Column(length = 2000)
    private String mensaje;

    @Column(name = "respondido_en")
    private LocalDateTime respondidoEn;

    @CreationTimestamp
    @Column(name = "creado_en", nullable = false, updatable = false)
    private LocalDateTime creadoEn;
}
