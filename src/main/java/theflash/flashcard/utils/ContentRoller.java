package theflash.flashcard.utils;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import theflash.helper.TheFlashProperties;

public class ContentRoller {

  private static final Logger logger = LoggerFactory.getLogger(ContentRoller.class);

  public static String lookupUrl(String dictUrl, String word) {
    word = word.replaceAll(" ", "%20");
    return String.format(dictUrl, word);
  }

  public static Document getDocument(String url) {
    Document document = null;
    try {
      if (!TheFlashProperties.PROXY_ADDRESS.isEmpty() && TheFlashProperties.PROXY_PORT != 0) {
        Proxy proxy = new Proxy(Type.HTTP,
            new InetSocketAddress(TheFlashProperties.PROXY_ADDRESS, TheFlashProperties.PROXY_PORT));
        document = Jsoup.connect(url).proxy(proxy).get();
      } else {
        document = Jsoup.connect(url).get();
      }
    } catch (IOException e) {
      logger.error("Exception: ", e);
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
      if (!TheFlashProperties.PROXY_ADDRESS.isEmpty() && TheFlashProperties.PROXY_PORT != 0) {
        Proxy proxy = new Proxy(Type.HTTP,
            new InetSocketAddress(TheFlashProperties.PROXY_ADDRESS, TheFlashProperties.PROXY_PORT));
        connection = site.openConnection(proxy);
      } else {
        connection = site.openConnection();
      }
      try (InputStream in = connection.getInputStream()) {
        Files.copy(in, Paths.get(target), StandardCopyOption.REPLACE_EXISTING);
      }
    } catch (IOException e) {
      logger.error("Exception: ", e);
    }
  }
}
