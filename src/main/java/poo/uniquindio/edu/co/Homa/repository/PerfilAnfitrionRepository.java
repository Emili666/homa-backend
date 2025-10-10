package poo.uniquindio.edu.co.Homa.repository; 
import poo.uniquindio.edu.co.Homa.entity.PerfilAnfitrionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PerfilAnfitrionRepository  extends JpaRepository<PerfilAnfitrionEntity, Long> {
}
