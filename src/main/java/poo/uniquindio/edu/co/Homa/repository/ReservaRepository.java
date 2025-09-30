package poo.uniquindio.edu.co.Homa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import poo.uniquindio.edu.co.Homa.model.Alojamiento;
import poo.uniquindio.edu.co.Homa.model.Reserva;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {
     List<Alojamiento> findByCapacidadGreaterThanEqual(int capacidad);
}
