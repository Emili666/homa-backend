package co.edu.uniquindio.homa.repository;

import co.edu.uniquindio.homa.model.entity.ContrasenaCodigoReinicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface ContrasenaCodigoReinicioRepository extends JpaRepository<ContrasenaCodigoReinicio, Long> {
    
    Optional<ContrasenaCodigoReinicio> findByCodigoAndUsuarioId(String codigo, String usuarioId);
    
    void deleteByUsuarioId(String usuarioId);
    
    void deleteByCreadoEnBefore(LocalDateTime fechaLimite);
}
