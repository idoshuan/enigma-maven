package enigma.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "enigma")
@EntityScan(basePackages = "enigma.dal.entities")
@EnableJpaRepositories(basePackages = "enigma.dal.repositories")
public class EnigmaApplication {
    public static void main(String[] args) {
        SpringApplication.run(EnigmaApplication.class, args);
    }
}
