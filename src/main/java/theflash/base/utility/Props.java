package theflash.base.utility;

import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;

public class Props {

  public static String PROXY_ADDRESS;

  public static String PROXY_PORT;

  public static void IntialProperties(String path) {

    Properties properties = null;
    try {
      properties = Props.LoadProperties(path);
    } catch (IOException e) {
      e.printStackTrace();
    }

    PROXY_ADDRESS = properties.getProperty("theflash.proxy.address");
    PROXY_PORT = properties.getProperty("theflash.proxy.port");
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
