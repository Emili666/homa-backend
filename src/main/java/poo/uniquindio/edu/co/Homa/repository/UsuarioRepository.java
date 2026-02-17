package poo.uniquindio.edu.co.Homa.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import poo.uniquindio.edu.co.Homa.model.entity.Usuario;
import poo.uniquindio.edu.co.Homa.model.enums.EstadoUsuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findByEmailAndEstado(String email, EstadoUsuario estado);

    boolean existsByEmail(String email);
    Optional<Usuario> findByCodigoActivacion(String codigoActivacion);

}
