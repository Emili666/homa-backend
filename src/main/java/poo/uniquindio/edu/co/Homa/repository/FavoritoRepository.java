package poo.uniquindio.edu.co.homa.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import poo.uniquindio.edu.co.homa.model.entity.Favorito;

@Repository
public interface FavoritoRepository extends JpaRepository<Favorito, Long> {

    Optional<Favorito> findByUsuarioIdAndAlojamientoId(Long usuarioId, Long alojamientoId);

    Page<Favorito> findByUsuarioId(Long usuarioId, Pageable pageable);

    long countByAlojamientoId(Long alojamientoId);

    boolean existsByUsuarioIdAndAlojamientoId(Long usuarioId, Long alojamientoId);

    void deleteByUsuarioIdAndAlojamientoId(Long usuarioId, Long alojamientoId);
}
