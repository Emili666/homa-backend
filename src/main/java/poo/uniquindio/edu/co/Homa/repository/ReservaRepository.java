package poo.uniquindio.edu.co.homa.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import poo.uniquindio.edu.co.homa.model.entity.Reserva;
import poo.uniquindio.edu.co.homa.model.enums.EstadoReserva;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    
    Page<Reserva> findByHuespedId(String huespedId, Pageable pageable);
    
    Page<Reserva> findByAlojamientoId(Long alojamientoId, Pageable pageable);
    
    Page<Reserva> findByAlojamientoAnfitrionId(String anfitrionId, Pageable pageable);
    
    @Query("SELECT r FROM Reserva r WHERE r.alojamiento.id = :alojamientoId " +
           "AND r.estado IN ('CONFIRMADA', 'PENDIENTE') " +
           "AND ((r.fechaEntrada <= :fechaSalida AND r.fechaSalida >= :fechaEntrada))")
    List<Reserva> findReservasConflictivas(
        @Param("alojamientoId") Long alojamientoId,
        @Param("fechaEntrada") LocalDate fechaEntrada,
        @Param("fechaSalida") LocalDate fechaSalida
    );
    
    @Query("SELECT COUNT(r) FROM Reserva r WHERE r.alojamiento.id = :alojamientoId " +
           "AND r.estado = 'CONFIRMADA' " +
           "AND r.fechaEntrada >= :fechaInicio AND r.fechaSalida <= :fechaFin")
    Long contarReservasPorPeriodo(
        @Param("alojamientoId") Long alojamientoId,
        @Param("fechaInicio") LocalDate fechaInicio,
        @Param("fechaFin") LocalDate fechaFin
        
    );
    
    boolean existsByHuesped_IdAndAlojamiento_IdAndEstado(
        Long huespedId, 
        Long alojamientoId, 
        EstadoReserva estado
    );
}
