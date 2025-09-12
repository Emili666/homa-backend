package poo.uniquindio.edu.co.Homa.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import poo.uniquindio.edu.co.Homa.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // Puedes agregar consultas personalizadas si es necesario
    boolean existsByEmail(String email);
}
