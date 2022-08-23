package sit.int221.at3.config;

import org.modelmapper.ModelMapper;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import sit.int221.at3.utils.ListMapper;

@SpringBootApplication
@Configuration
public class ApplicationConfig {
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public ListMapper listMapper() {
        return ListMapper.getInstance();
    }

//    @Bean
//    public WebMvcConfigurer corsConfigurer() {
//        return new WebMvcConfigurer() {
//            @Override
//            public void addCorsMappings(CorsRegistry registry) {
//                registry.addMapping("/events").allowedOrigins("http://localhost:3000");
//                registry.addMapping("/events/{id}").allowedOrigins("http://localhost:3000");
//                registry.addMapping("/categories/").allowedOrigins("http://localhost:3000");
//                registry.addMapping("/categories/{id}").allowedOrigins("http://localhost:3000");
//            }
//        };
//    }
}
