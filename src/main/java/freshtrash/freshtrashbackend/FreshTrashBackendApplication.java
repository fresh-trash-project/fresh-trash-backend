package freshtrash.freshtrashbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
public class FreshTrashBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(FreshTrashBackendApplication.class, args);
    }

}
