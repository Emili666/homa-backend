package poo.uniquindio.edu.co.Homa.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

        @Override
        public void run(String... args) {
                // Datos de prueba desactivados para producción
                log.info("DataInitializer: sin datos de prueba.");
        }
}
