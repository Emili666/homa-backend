package poo.uniquindio.edu.co.homa.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import poo.uniquindio.edu.co.homa.model.entity.Usuario;
import poo.uniquindio.edu.co.homa.model.enums.EstadoUsuario;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, String> {
    
    Optional<Usuario> findByEmail(String email);
      
    
    Optional<Usuario> findByEmailAndEstado(String email, EstadoUsuario estado);
    
    boolean existsByEmail(String email);


    Optional<Usuario> findById(Long anfitrionId);
}
