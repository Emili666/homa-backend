package poo.uniquindio.edu.co.Homa.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import poo.uniquindio.edu.co.Homa.model.entity.Alojamiento;
import poo.uniquindio.edu.co.Homa.model.entity.Favorito;
import poo.uniquindio.edu.co.Homa.model.entity.Usuario;

@Repository
public interface FavoritoRepository extends JpaRepository<Favorito, Long> {

    Optional<Favorito> findByUsuarioIdAndAlojamientoId(Long usuarioId, Long alojamientoId);

    Optional<Favorito> findByUsuarioAndAlojamiento(Usuario usuario, Alojamiento alojamiento);

    Page<Favorito> findByUsuarioId(Long usuarioId, Pageable pageable);

    long countByAlojamientoId(Long alojamientoId);

    boolean existsByUsuarioIdAndAlojamientoId(Long usuarioId, Long alojamientoId);

    void deleteByUsuarioIdAndAlojamientoId(Long usuarioId, Long alojamientoId);
}
