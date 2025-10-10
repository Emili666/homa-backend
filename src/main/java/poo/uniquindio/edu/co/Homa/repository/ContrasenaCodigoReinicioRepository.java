package poo.uniquindio.edu.co.Homa.repository;
import poo.uniquindio.edu.co.Homa.entity.ContrasenaCodigoReinicioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContrasenaCodigoReinicioRepository extends JpaRepository<ContrasenaCodigoReinicioEntity, Long> {
   Optional<ContrasenaCodigoReinicioEntity> findByUsuario_Email(String email);
}
