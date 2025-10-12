package poo.uniquindio.edu.co.homa.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import poo.uniquindio.edu.co.homa.model.entity.PerfilAnfitrion;

import java.util.Optional;
import java.util.concurrent.atomic.LongAdder;

@Repository
public interface PerfilAnfitrionRepository extends JpaRepository<PerfilAnfitrion, Long> {
    
    Optional<PerfilAnfitrion> findByUsuarioId(Long usuarioId);
}
