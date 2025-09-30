package poo.uniquindio.edu.co.Homa.repository;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import poo.uniquindio.edu.co.Homa.model.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Verificar si ya existe un usuario con un email (útil para registro/login)
    boolean existsByEmail(String email);

    // Buscar usuario por email (para login o asignar reservas)
    Optional<Usuario> findByEmail(String email);
}
