package poo.uniquindio.edu.co.Homa.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;
import poo.uniquindio.edu.co.Homa.repository.UsuarioRepository;
import poo.uniquindio.edu.co.Homa.model.entity.Usuario;
import poo.uniquindio.edu.co.Homa.model.enums.EstadoUsuario;
import poo.uniquindio.edu.co.Homa.model.enums.RolUsuario;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

        private final UsuarioRepository usuarioRepository;
        private final PasswordEncoder passwordEncoder;
        private final org.springframework.jdbc.core.JdbcTemplate jdbcTemplate;

        @Value("${admin.email:#{null}}")
        private String adminEmail;

        @Value("${admin.password:#{null}}")
        private String adminPassword;

        @Override
        public void run(String... args) {
                log.info("DataInitializer: alterando tabla usuario para rol...");
                try {
                        jdbcTemplate.execute("ALTER TABLE usuario MODIFY COLUMN rol VARCHAR(50);");
                        log.info("Columna rol modificada a VARCHAR.");
                } catch (Exception e) {
                        log.warn("No se pudo alterar rol: {}", e.getMessage());
                }

                if (adminEmail == null || adminEmail.isBlank()) {
                        log.warn("Variable ADMIN_EMAIL no configurada. Se omite creacion del administrador.");
                        return;
                }
                if (adminPassword == null || adminPassword.isBlank()) {
                        log.warn("Variable ADMIN_PASSWORD no configurada. Se omite creacion del administrador.");
                        return;
                }

                log.info("DataInitializer: verificando administrador principal...");
                if (!usuarioRepository.existsByEmail(adminEmail)) {
                        Usuario admin = Usuario.builder()
                                        .nombre("Administrador Principal")
                                        .email(adminEmail)
                                        .contrasena(passwordEncoder.encode(adminPassword))
                                        .telefono("3000000000")
                                        .rol(RolUsuario.Administrador)
                                        .estado(EstadoUsuario.ACTIVO)
                                        .esAnfitrion(false)
                                        .codigoActivacion(UUID.randomUUID().toString())
                                        .creadoEn(LocalDateTime.now())
                                        .build();
                        usuarioRepository.save(admin);
                        log.info("Usuario ADMIN creado: {}", adminEmail);
                }
        }
}
