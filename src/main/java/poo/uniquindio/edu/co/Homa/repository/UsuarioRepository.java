package poo.uniquindio.edu.co.Homa.repository;
import poo.uniquindio.edu.co.Homa.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioEntity, String> {
    Optional<UsuarioEntity> findByEmail(String email);
}
