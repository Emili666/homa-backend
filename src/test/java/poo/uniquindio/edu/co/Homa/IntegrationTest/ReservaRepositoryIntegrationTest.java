package poo.uniquindio.edu.co.Homa.IntegrationTest;

/*
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import poo.uniquindio.edu.co.homa.model.entity.Usuario;
import poo.uniquindio.edu.co.homa.repository.UsuarioRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@DataJpaTest
class UsuarioRepositoryIntegrationTest {

    // 🔹 Crea y levanta automáticamente una DB MariaDB
    @Container
    static final MariaDBContainer<?> mariaDB = new MariaDBContainer<>("mariadb:11.3.2")
            .withDatabaseName("homa_test_db")
            .withUsername("testuser")
            .withPassword("testpass");

    @DynamicPropertySource
    static void mariaDbProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mariaDB::getJdbcUrl);
        registry.add("spring.datasource.username", mariaDB::getUsername);
        registry.add("spring.datasource.password", mariaDB::getPassword);
    }

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    @DisplayName("Guardar y recuperar un usuario correctamente")
    void guardarYBuscarUsuario() {
        Usuario u = new Usuario();
        u.setNombre("Carlos Integracion");
        u.setEmail("carlos.integracion@example.com");
        u.setContrasena("Segura123!");

        Usuario saved = usuarioRepository.save(u);
        assertNotNull(saved.getId());

        Optional<Usuario> encontrado = usuarioRepository.findByEmail("carlos.integracion@example.com");
        assertTrue(encontrado.isPresent());
        assertEquals("Carlos Integracion", encontrado.get().getNombre());
    }

    @Test
    @DisplayName("findByEmail debe devolver vacío si no existe")
    void buscarEmailInexistente() {
        Optional<Usuario> result = usuarioRepository.findByEmail("noexiste@example.com");
        assertTrue(result.isEmpty());
    }
}
*/