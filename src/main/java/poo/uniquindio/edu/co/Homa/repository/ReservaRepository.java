package poo.uniquindio.edu.co.Homa.repository;

import poo.uniquindio.edu.co.Homa.entity.ReservaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservaRepository extends JpaRepository<ReservaEntity, Long> {
}
