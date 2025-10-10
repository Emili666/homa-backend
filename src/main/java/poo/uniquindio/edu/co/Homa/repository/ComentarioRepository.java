package poo.uniquindio.edu.co.Homa.repository;


import poo.uniquindio.edu.co.Homa.entity.ComentarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComentarioRepository extends JpaRepository<ComentarioEntity, Long> {
}
