package freshtrash.freshtrashbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@SpringBootApplication
public class FreshTrashBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(FreshTrashBackendApplication.class, args);
    }
}
