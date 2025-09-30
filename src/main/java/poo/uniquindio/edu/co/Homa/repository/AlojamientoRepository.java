
package poo.uniquindio.edu.co.Homa.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import poo.uniquindio.edu.co.Homa.model.Alojamiento;

@Repository
public interface AlojamientoRepository extends JpaRepository<Alojamiento, Long> {
    // Buscar alojamientos por email del anfitrión
    List<Alojamiento> findByAnfitrion_Email(String email);
}
