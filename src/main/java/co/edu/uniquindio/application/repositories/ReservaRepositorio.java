package co.edu.uniquindio.application.repositories;

import co.edu.uniquindio.application.models.entitys.Reserva;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReservaRepositorio extends JpaRepository<Reserva, Long> {

    Page<Reserva> findByHuespedId(String huespedId, Pageable pageable);
    Optional<Reserva> findByHuespedIdAndAlojamientoId(String huespedId, Long alojamientoId);
}
