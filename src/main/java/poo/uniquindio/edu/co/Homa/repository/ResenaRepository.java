package poo.uniquindio.edu.co.homa.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import poo.uniquindio.edu.co.homa.model.entity.Resena;

@Repository
public interface ResenaRepository extends JpaRepository<Resena, Long> {
    
    Page<Resena> findByAlojamientoIdOrderByCreadoEnDesc(Long alojamientoId, Pageable pageable);
    
    boolean existsByUsuarioIdAndAlojamientoId(String usuarioId, Long alojamientoId);
    
    @Query("SELECT AVG(r.calificacion) FROM Resena r WHERE r.alojamiento.id = :alojamientoId")
    Double calcularPromedioCalificacion(@Param("alojamientoId") Long alojamientoId);
}
