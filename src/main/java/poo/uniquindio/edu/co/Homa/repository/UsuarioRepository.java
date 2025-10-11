package poo.uniquindio.edu.co.homa.repository;

import co.edu.uniquindio.homa.model.entity.Usuario;
import co.edu.uniquindio.homa.model.enums.EstadoUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, String> {
    
    Optional<Usuario> findByEmail(String email);
    
    Optional<Usuario> findByEmailAndEstado(String email, EstadoUsuario estado);
    
    boolean existsByEmail(String email);
}
