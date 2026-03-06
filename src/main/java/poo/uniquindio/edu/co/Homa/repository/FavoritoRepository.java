package poo.uniquindio.edu.co.Homa.repository;

import java.util.Optional;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query("SELECT f.alojamiento.id, COUNT(f) FROM Favorito f WHERE f.alojamiento.id IN :alojamientoIds GROUP BY f.alojamiento.id")
    List<Object[]> countByAlojamientoIdIn(@Param("alojamientoIds") List<Long> alojamientoIds);

    boolean existsByUsuarioIdAndAlojamientoId(Long usuarioId, Long alojamientoId);

    void deleteByUsuarioIdAndAlojamientoId(Long usuarioId, Long alojamientoId);
}
