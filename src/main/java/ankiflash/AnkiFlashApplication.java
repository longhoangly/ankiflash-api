package ankiflash;

import ankiflash.utility.exception.ErrorHandler;
import java.lang.reflect.Field;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableAsync
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@PropertySource("classpath:ankiflash.properties")
public class AnkiFlashApplication {

  private static final Logger logger = LoggerFactory.getLogger(AnkiFlashApplication.class);

  private static ApplicationContext applicationContext;

  @Value("${spring.security.allowed.origins}")
  private String origins;

  public static void main(String[] args) {
    applicationContext = SpringApplication.run(AnkiFlashApplication.class, args);
    displayInitInfo();
    logger.info("AnkiFlash Application Started...");
  }

  private static void displayInitInfo() {
    String[] allBeanNames = applicationContext.getBeanDefinitionNames();
    for (String beanName : allBeanNames) {
      logger.info(beanName);
    }

    Class clazz = applicationContext.getBean("ankiFlashProps").getClass();
    Field[] fields = clazz.getDeclaredFields();
    for (Field field : fields) {
      try {
        logger.info("{} = {}", field.getName(), field.get(applicationContext).toString());
      } catch (IllegalAccessException e) {
        ErrorHandler.log(e);
      }
    }
  }

  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins(origins.split(";")).allowedMethods("*");
      }
    };
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    final CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(Arrays.asList(origins.split(";")));
    configuration.setAllowedMethods(Arrays.asList("HEAD", "GET", "POST", "PUT", "DELETE", "PATCH"));

    // setAllowCredentials(true) is important, otherwise:
    // The value of the 'Access-Control-Allow-Origin' header in the response must not be
    // the wildcard '*' when the request's credentials mode is 'include'.
    configuration.setAllowCredentials(true);

    // setAllowedHeaders is important. Without it, OPTIONS pre-flight request
    // will fail with error code 403 (Invalid CORS)
    configuration.setAllowedHeaders(
        Arrays.asList("Authorization", "Cache-Control", "Content-Type", "Pragma", "Expires"));
    configuration.setMaxAge(86400L);
    final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}
