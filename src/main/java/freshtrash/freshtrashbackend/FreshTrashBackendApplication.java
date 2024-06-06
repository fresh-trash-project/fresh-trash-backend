package freshtrash.freshtrashbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableRetry
@EnableScheduling
@SpringBootApplication
@ConfigurationPropertiesScan
public class FreshTrashBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(FreshTrashBackendApplication.class, args);
    }
}
