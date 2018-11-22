package theflash.helper;

import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;

public class PropertiesHelper {

  public static String PROXY_ADDRESS;

  public static String PROXY_PORT;

  public static String ANKI_DIR_FLASHCARDS;

  public static void IntialProperties(String path) {
    Properties properties = null;
    try {
      properties = PropertiesHelper.LoadProperties(path);
    } catch (IOException e) {
      e.printStackTrace();
    }
    PROXY_ADDRESS = properties.getProperty("theflash.proxy.address");
    PROXY_PORT = properties.getProperty("theflash.proxy.port");
    ANKI_DIR_FLASHCARDS = properties.getProperty("theflash.anki.root");
  }

  public static void IntialProperties() {
    IntialProperties("application.properties");
  }

  public static Properties LoadProperties(String path) throws IOException {
    Properties properties = new Properties();
    properties.load(new StringReader(path));
    return properties;
  }

  public static Properties LoadProperties() throws IOException {
    return LoadProperties("application.properties");
  }
}
