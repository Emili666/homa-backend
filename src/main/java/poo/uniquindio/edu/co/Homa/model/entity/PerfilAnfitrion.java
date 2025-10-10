package co.edu.uniquindio.homa.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "perfil_anfitrion", indexes = {
    @Index(name = "idx_usuario_id", columnList = "usuario_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PerfilAnfitrion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private Usuario usuario;

    @Column(name = "documento_legal", length = 255)
    private String documentoLegal;

    @Column(name = "sobre_mi", columnDefinition = "TEXT")
    private String sobreMi;
}
