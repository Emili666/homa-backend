package poo.uniquindio.edu.co.Homa.entity;
import java.time.LocalDateTime;

import co.edu.uniquindio.application.models.vo.Respuesta;
import jakarta.persistence.*;
import lombok.*;
import main.java.poo.uniquindio.edu.co.Homa.entity.Alojamiento;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class ComentarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Float calificacion;

    @Column(length = 2000)
    private String comentario;

    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime creadoEn;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "alojamiento_id", nullable = false)
    private Alojamiento alojamiento;

    @Embedded
    private Respuesta respuesta;
}
