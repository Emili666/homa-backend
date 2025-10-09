package poo.uniquindio.edu.co.Homa.service;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.Collections;

@Service // 👈 Esto es lo que le dice a Spring que cree el bean
public class UserDetailsServiceImpl implements UserDetailsService {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (username == null || username.isBlank()) {
            throw new UsernameNotFoundException("El nombre de usuario es obligatorio");
        }

        return User.builder()
                .username(username)
                .password(passwordEncoder.encode("1234")) // temporal
                .authorities(Collections.emptyList())
                .build();
    }

    // 👇 Esto lo agregamos para que encaje con tu filtro
    public UserDetails cargarUsuarioPorUsername(String username) {
        return loadUserByUsername(username);
    }
}