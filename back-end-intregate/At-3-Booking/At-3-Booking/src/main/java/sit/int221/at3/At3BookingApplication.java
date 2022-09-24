package sit.int221.at3;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class At3BookingApplication {
    public static void main(String[] args) {
        SpringApplication.run(At3BookingApplication.class, args);
    }
}