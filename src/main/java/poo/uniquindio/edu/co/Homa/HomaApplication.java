package poo.uniquindio.edu.co.Homa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "poo.uniquindio.edu.co.Homa.repository")
@EntityScan(basePackages = "poo.uniquindio.edu.co.Homa.model.entity")
@ComponentScan(basePackages = "poo.uniquindio.edu.co.Homa")
public class HomaApplication {

    public static void main(String[] args) {
        SpringApplication.run(HomaApplication.class, args);
    }
}
  