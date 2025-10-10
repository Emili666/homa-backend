package co.edu.uniquindio.homa.repository;

import co.edu.uniquindio.homa.model.entity.PerfilAnfitrion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PerfilAnfitrionRepository extends JpaRepository<PerfilAnfitrion, Long> {
    
    Optional<PerfilAnfitrion> findByUsuarioId(String usuarioId);
}
