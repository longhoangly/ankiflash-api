package theflash.helper;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertiesHelper {

  private static final Logger logger = LoggerFactory.getLogger(PropertiesHelper.class);

  public static String PROXY_ADDRESS;
  public static String PROXY_PORT;
  public static String ANKI_DIR_FLASHCARDS;

  public static void intialProperties() {
    intialProperties("application.properties");
  }

  public static void intialProperties(String path) {
    Properties properties = loadProperties(path);

    PROXY_ADDRESS = properties.getProperty("theflash.proxy.address");
    PROXY_PORT = properties.getProperty("theflash.proxy.port");
    ANKI_DIR_FLASHCARDS = properties.getProperty("theflash.anki.root");
  }

  public static Properties loadProperties(String path) {
    Properties properties = new Properties();
    try {
      properties.load(new FileInputStream(path));
    } catch (IOException e) {
      logger.error(e.getMessage(), e);
    }
    return properties;
  }
}
