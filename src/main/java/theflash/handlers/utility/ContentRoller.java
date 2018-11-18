package theflash.handlers.utility;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ContentRoller {

  public static String lookupUrl(String dictUrl, String word) {

    word = word.replaceAll(" ", "%20");
    return String.format(dictUrl, word);
  }

  public static Document getDocument(String url) {

    Document document = null;
    try {
      if (Props.PROXY_ADDRESS != null && !Props.PROXY_ADDRESS.isEmpty() && Props.PROXY_PORT != null
          && !Props.PROXY_PORT.isEmpty()) {
        Proxy proxy = new Proxy(Type.HTTP,
            new InetSocketAddress(Props.PROXY_ADDRESS, Integer.parseInt(Props.PROXY_PORT)));
        document = Jsoup.connect(url).proxy(proxy).get();

      } else {
        document = Jsoup.connect(url).get();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    return document;
  }

  public static Elements getElements(Document doc, String selector) {

    return doc.select(selector);
  }

  public static Element getElement(Document doc, String selector, int index) {

    Elements elements = doc.select(selector);
    return elements.size() > 0 ? elements.get(index) : null;
  }

  public static String getText(Document doc, String selector, int index) {

    Element element = getElement(doc, selector, index);
    return element != null ? element.text() : "";
  }

  public static String getInnerHtml(Document doc, String selector, int index) {

    Element element = getElement(doc, selector, index);
    return element != null ? element.html() : "";
  }

  public static String getAttribute(Document doc, String selector, int index, String attr) {

    Element element = getElement(doc, selector, index);
    return element != null ? element.attr(attr) : "";
  }

  public static void download(String url, String target) {

    try {
      URL site = new URL(url);
      URLConnection connection;
      if (Props.PROXY_ADDRESS != null && !Props.PROXY_ADDRESS.isEmpty() && Props.PROXY_PORT != null
          && !Props.PROXY_PORT.isEmpty()) {
        Proxy proxy = new Proxy(Type.HTTP,
            new InetSocketAddress(Props.PROXY_ADDRESS, Integer.parseInt(Props.PROXY_PORT)));
        connection = site.openConnection(proxy);
      } else {
        connection = site.openConnection();
      }

      try (InputStream in = connection.getInputStream()) {
        Files.copy(in, Paths.get(target), StandardCopyOption.REPLACE_EXISTING);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
