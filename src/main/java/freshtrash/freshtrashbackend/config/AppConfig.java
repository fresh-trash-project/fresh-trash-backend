package freshtrash.freshtrashbackend.config;

import freshtrash.freshtrashbackend.dto.properties.S3Properties;
import freshtrash.freshtrashbackend.service.FileService;
import freshtrash.freshtrashbackend.service.LocalFileService;
import freshtrash.freshtrashbackend.service.S3Service;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.util.Arrays;

@Configuration
public class AppConfig {
    @Bean
    public S3Client s3Client(S3Properties s3Properties) {
        return S3Client.builder()
                .region(Region.AP_NORTHEAST_2)
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(s3Properties.accessKey(), s3Properties.secretKey())))
                .build();
    }

    @Bean
    public FileService fileService(Environment env, S3Service s3Service, LocalFileService localFileService) {
        return selectBean(env, s3Service, localFileService);
    }

    private <T> T selectBean(Environment env, T prodBean, T localBean) {
        String activatedProfile =
                Arrays.stream(env.getActiveProfiles()).findFirst().orElse("local");
        if (activatedProfile.startsWith("prod")) {
            return prodBean;
        } else {
            return localBean;
        }
    }
}
