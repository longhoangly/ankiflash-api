package theflash;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import theflash.helper.PropertiesHelper;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class Application {

  private static final Logger logger = LoggerFactory.getLogger(Application.class);

  @Value("${spring.security.allowed-origins}")
  private String origins;

  public static void main(String[] args) {
    PropertiesHelper.intialProperties("src/main/resources/application.properties");

    SpringApplication.run(Application.class, args);
    logger.info("Application Started");
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