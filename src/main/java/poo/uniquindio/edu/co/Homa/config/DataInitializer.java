package poo.uniquindio.edu.co.homa.config;

import java.util.UUID;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import poo.uniquindio.edu.co.homa.model.entity.Usuario;
import poo.uniquindio.edu.co.homa.model.enums.EstadoUsuario;
import poo.uniquindio.edu.co.homa.model.enums.RolUsuario;
import poo.uniquindio.edu.co.homa.repository.UsuarioRepository;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (usuarioRepository.findByEmail("admin@homa.com").isEmpty()) {
            Usuario admin = new Usuario();
            admin.setNombre("Anfritrion");
            admin.setEmail("admin@homa.com");
            admin.setContrasena(passwordEncoder.encode("admin123")); // password encriptado
            admin.setRol(RolUsuario.Anfitrion);
            admin.setEstado(EstadoUsuario.ACTIVO);
            admin.setCodigoActivacion(UUID.randomUUID().toString());

            usuarioRepository.save(admin);
            System.out.println("Usuario admin creado: admin@homa.com / admin123");
        }
    }
}
