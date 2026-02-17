package poo.uniquindio.edu.co.Homa.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import poo.uniquindio.edu.co.Homa.model.entity.ContrasenaCodigoReinicio;

@Repository
public interface ContrasenaCodigoReinicioRepository extends JpaRepository<ContrasenaCodigoReinicio, Long> {

    Optional<ContrasenaCodigoReinicio> findByCodigoAndUsuarioId(String codigo, Long usuarioId);

    Optional<ContrasenaCodigoReinicio> findByCodigo(String codigo);

    void deleteByUsuarioId(Long usuarioId);

    void deleteByCreadoEnBefore(LocalDateTime fechaLimite);
}
