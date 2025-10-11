package poo.uniquindio.edu.co.homa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "poo.uniquindio.edu.co.homa.repository")
@EntityScan(basePackages = "poo.uniquindio.edu.co.homa.model")
@ComponentScan(basePackages = "poo.uniquindio.edu.co.homa")
public class HomaApplication {

    public static void main(String[] args) {
        SpringApplication.run(HomaApplication.class, args);
    }
}
