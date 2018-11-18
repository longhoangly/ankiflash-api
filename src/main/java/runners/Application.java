package runners;

import java.io.IOException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import theflash.handlers.utility.Props;

@SpringBootApplication
@ComponentScan(basePackages = "theflash.controllers")
public class Application {

  public static void main(String[] args) throws IOException {

    Props.IntialProperties();
    SpringApplication.run(Application.class, args);
  }
}