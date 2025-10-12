package poo.uniquindio.edu.co.homa.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import poo.uniquindio.edu.co.homa.model.enums.EstadoAlojamiento;
import poo.uniquindio.edu.co.homa.model.enums.Servicio;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "alojamiento", indexes = {
    @Index(name = "idx_anfitrion_id", columnList = "anfitrion_id"),
    @Index(name = "idx_ciudad", columnList = "ciudad"),
    @Index(name = "idx_estado", columnList = "estado"),
    @Index(name = "idx_precio", columnList = "precio_por_noche")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Alojamiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(length = 2000)
    private String descripcion;

    @Column(nullable = false, length = 255)
    private String ciudad;

    @Column(nullable = false, length = 255)
    private String direccion;

    private Float latitud;

    private Float longitud;

    @Column(name = "precio_por_noche", nullable = false)
    private Float precioPorNoche;

    @Column(name = "max_huespedes", nullable = false)
    private Integer maxHuespedes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private EstadoAlojamiento estado = EstadoAlojamiento.ACTIVO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "anfitrion_id", nullable = false)
    private Usuario anfitrion;

    @CreationTimestamp
    @Column(name = "creado_en", nullable = false, updatable = false)
    private LocalDateTime creadoEn;

    @ElementCollection
    @CollectionTable(name = "alojamiento_imagenes",
        joinColumns = @JoinColumn(name = "alojamiento_id"),
        indexes = @Index(name = "idx_orden", columnList = "orden"))
    @Column(name = "imagenes", length = 255)
    @OrderColumn(name = "orden")
    @Builder.Default
    private List<String> imagenes = new ArrayList<>();

    // ✅ CAMBIO IMPORTANTE AQUÍ
    @ElementCollection(targetClass = Servicio.class)
    @CollectionTable(name = "alojamiento_servicios",
        joinColumns = @JoinColumn(name = "alojamiento_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "servicio")
    @Builder.Default
    private List<Servicio> servicios = new ArrayList<>();

    @OneToMany(mappedBy = "alojamiento", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Reserva> reservas = new ArrayList<>();

    @OneToMany(mappedBy = "alojamiento", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Resena> resenas = new ArrayList<>();
}
