package poo.uniquindio.edu.co.homa.service;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import poo.uniquindio.edu.co.homa.repository.UsuarioRepository;
import poo.uniquindio.edu.co.homa.model.entity.Usuario;

@Service("customUserDetailsService")
@RequiredArgsConstructor
@Primary
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email));

        return User.builder()
                .username(usuario.getEmail())
                .password(usuario.getContrasena())
                .roles(usuario.getRol().name())
                .build();
    }
}
