package poo.uniquindio.edu.co.homa.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import poo.uniquindio.edu.co.homa.model.entity.ContrasenaCodigoReinicio;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface ContrasenaCodigoReinicioRepository extends JpaRepository<ContrasenaCodigoReinicio, Long> {
    
    Optional<ContrasenaCodigoReinicio> findByCodigoAndUsuarioId(String codigo, String usuarioId);
    
    void deleteByUsuarioId(String usuarioId);
    
    void deleteByCreadoEnBefore(LocalDateTime fechaLimite);
}
