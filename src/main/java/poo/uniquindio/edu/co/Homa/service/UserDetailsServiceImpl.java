package poo.uniquindio.edu.co.Homa.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserDetailsServiceImpl {
      UserDetails cargarUsuarioPorUsername(String id) throws UsernameNotFoundException;

      UserDetails loadUserByUsername(String username);
}
