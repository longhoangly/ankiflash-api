package theflash;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableAsync
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class TheFlashApplication {

  private static final Logger logger = LoggerFactory.getLogger(TheFlashApplication.class);

  @Value("${spring.security.allowed.origins}")
  private String origins;

  public static void main(String[] args) {
    SpringApplication.run(TheFlashApplication.class, args);
    logger.info("TheFlashApplication Started");
  }

  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins(origins);
      }
    };
  }
}