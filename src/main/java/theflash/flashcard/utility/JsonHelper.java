package theflash.flashcard.utility;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.nio.charset.Charset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonHelper {

  private static final Logger logger = LoggerFactory.getLogger(JsonHelper.class);

  private static String readAll(Reader reader) {

    StringBuilder sb = new StringBuilder();
    int intValueOfChar;
    try {
      while ((intValueOfChar = reader.read()) != -1) {
        sb.append((char) intValueOfChar);
      }
      reader.close();
    } catch (IOException e) {
      logger.error("Exception Occurred: ", e);
    }
    return sb.toString();
  }

  public static JsonObject getRequest(String url) {

    return getRequest(url, null);
  }

  public static JsonObject getRequest(String url, Proxy proxy) {

    JsonObject json = new JsonObject();
    try {
      InputStream is = proxy == null ? new URL(url).openStream() : new URL(url).openConnection(proxy).getInputStream();
      BufferedReader br = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
      String jsonText = readAll(br);
      json = new JsonParser().parse(jsonText).getAsJsonObject();
      is.close();
    } catch (IOException e) {
      logger.error("Exception Occurred: ", e);
    }

    return json;
  }

  public static JsonObject postRequest(String url, String body) {

    return postRequest(url, body, null);
  }

  public static JsonObject postRequest(String url, String body, Proxy proxy) {

    JsonObject json = new JsonObject();
    try {
      URL ur = new URL(url);
      HttpURLConnection con =
          proxy == null ? (HttpURLConnection) ur.openConnection() : (HttpURLConnection) ur.openConnection(proxy);
      con.setRequestMethod("POST");
      con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
      con.setRequestProperty("Accept", "application/json");

      // To write content to the connection output stream
      con.setDoOutput(true);
      try (OutputStream os = con.getOutputStream()) {
        byte[] bodyBytes = body.getBytes("utf-8");
        os.write(bodyBytes, 0, bodyBytes.length);
      }

      InputStream is = con.getInputStream();
      BufferedReader br = new BufferedReader(new InputStreamReader(is, "utf-8"));
      String jsonText = readAll(br);
      json = new JsonParser().parse(jsonText).getAsJsonObject();
      is.close();
    } catch (IOException e) {
      logger.error("Exception Occurred: ", e);
    }

    return json;
  }
}
