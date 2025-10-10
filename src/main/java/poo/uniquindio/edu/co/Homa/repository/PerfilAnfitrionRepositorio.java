package poo.uniquindio.edu.co.Homa.repository; 
import co.edu.uniquindio.application.models.entitys.PerfilAnfitrion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PerfilAnfitrionRepositorio  extends JpaRepository<PerfilAnfitrion, Long> {
}
