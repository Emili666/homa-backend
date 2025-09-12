
package poo.uniquindio.edu.co.Homa.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import poo.uniquindio.edu.co.Homa.model.Usuario;
import poo.uniquindio.edu.co.Homa.repository.UsuarioRepository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UsuarioDao {

    private final UsuarioRepository usuarioRepository;

    public Usuario guardar(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public boolean existePorEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    public void eliminar(Long id) {
        usuarioRepository.deleteById(id);
    }
}
