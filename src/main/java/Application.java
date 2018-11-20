import theflash.base.utility.Props;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan("theflash")
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class Application {

  private static final Logger logger = LoggerFactory.getLogger(Application.class);

  public static void main(String[] args) {

    Props.IntialProperties();
    SpringApplication.run(Application.class, args);
    logger.info("Application Started");
  }
}