package poo.uniquindio.edu.co.Homa.repository;


import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import poo.uniquindio.edu.co.Homa.model.entity.Alojamiento;
import poo.uniquindio.edu.co.Homa.model.entity.Resena;
import poo.uniquindio.edu.co.Homa.model.entity.Usuario;

@Repository
public interface ResenaRepository extends JpaRepository<Resena, Long> {
    
    Page<Resena> findByAlojamientoIdOrderByCreadoEnDesc(Long alojamientoId, Pageable pageable);
    
    boolean existsByUsuarioIdAndAlojamientoId(Long usuarioId, Long alojamientoId);
    
    Optional<Resena> findByAlojamientoAndUsuario(Alojamiento alojamiento, Usuario usuario);
    
    @Query("SELECT AVG(r.calificacion) FROM Resena r WHERE r.alojamiento.id = :alojamientoId")
    Double calcularPromedioCalificacion(@Param("alojamientoId") Long alojamientoId);

    @Query("SELECT r FROM Resena r WHERE r.esDestacado = true ORDER BY r.creadoEn DESC")
    Page<Resena> findDestacadas(Pageable pageable);

    Page<Resena> findByCalificacionGreaterThanEqualOrderByCreadoEnDesc(Integer calificacion, Pageable pageable);
}
