package sit.int221.at3.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "file")
@Configuration
@Getter
@Setter
public class FileStorageProperties {
    private String uploadDir;
}
