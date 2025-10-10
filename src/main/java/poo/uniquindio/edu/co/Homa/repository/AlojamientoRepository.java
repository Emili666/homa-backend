package poo.uniquindio.edu.co.homa.repository;

import co.edu.uniquindio.homa.model.entity.Alojamiento;
import co.edu.uniquindio.homa.model.enums.EstadoAlojamiento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlojamientoRepository extends JpaRepository<Alojamiento, Long> {
    Page<Alojamiento> findByEstado(EstadoAlojamiento estado, Pageable pageable);
    
    Page<Alojamiento> findByAnfitrionIdAndEstado(String anfitrionId, EstadoAlojamiento estado, Pageable pageable);
    
    Optional<Alojamiento> findByIdAndEstado(Long id, EstadoAlojamiento estado);
    
    @Query("SELECT a FROM Alojamiento a WHERE a.estado = :estado " +
           "AND (:ciudad IS NULL OR LOWER(a.ciudad) LIKE LOWER(CONCAT('%', :ciudad, '%'))) " +
           "AND (:precioMin IS NULL OR a.precioPorNoche >= :precioMin) " +
           "AND (:precioMax IS NULL OR a.precioPorNoche <= :precioMax) " +
           "AND (:maxHuespedes IS NULL OR a.maxHuespedes >= :maxHuespedes)")
    Page<Alojamiento> buscarAlojamientos(
        @Param("estado") EstadoAlojamiento estado,
        @Param("ciudad") String ciudad,
        @Param("precioMin") Float precioMin,
        @Param("precioMax") Float precioMax,
        @Param("maxHuespedes") Integer maxHuespedes,
        Pageable pageable
    );
}
