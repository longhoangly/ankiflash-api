package ankiflash.card.utility;

import com.google.gson.JsonObject;
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
import java.util.ArrayList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ankiflash.card.dto.Meaning;
import ankiflash.utility.TheFlashProperties;

public class HtmlHelper {

  private static final Logger logger = LoggerFactory.getLogger(HtmlHelper.class);

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

  public static Element getElement(Document doc, String selector, int index) {
    Elements elements = doc.select(selector);
    return elements.size() - index > 0 ? elements.get(index) : null;
  }

  public static Element getElement(Element element, String selector, int index) {
    Elements elements = element.select(selector);
    return elements.size() - index > 0 ? elements.get(index) : null;
  }

  public static String getText(Document doc, String selector, int index) {
    Element element = getElement(doc, selector, index);
    return element != null ? element.text() : "";
  }

  public static List<String> getTexts(Document doc, String selector) {
    Elements elements = doc.select(selector);
    List<String> texts = new ArrayList<>();
    for (Element element : elements) {
      texts.add(element.text());
    }
    return texts;
  }

  public static String getInnerHtml(Document doc, String selector, int index) {
    Element element = getElement(doc, selector, index);
    return element != null ? element.html() : "";
  }

  public static String getInnerHtml(Element element, String selector, int index) {
    Element elm = getElement(element, selector, index);
    return elm != null ? elm.html() : "";
  }

  public static String getOuterHtml(Document doc, String selector, int index) {
    Element element = getElement(doc, selector, index);
    return element != null ? element.outerHtml() : "";
  }

  public static String getOuterHtml(Element element, String selector, int index) {
    Element elm = getElement(element, selector, index);
    return elm != null ? elm.outerHtml() : "";
  }

  public static String getAttribute(Document doc, String selector, int index, String attr) {
    Element element = getElement(doc, selector, index);
    return element != null ? element.attr(attr) : "";
  }

  public static boolean download(String url, String target) {
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
      connection.addRequestProperty("User-Agent", "Mozilla/5.0 Gecko/20100101 Firefox/47.0");
      connection.setConnectTimeout(TheFlashProperties.CONNECTION_TIMEOUT);
      connection.setReadTimeout(TheFlashProperties.READ_TIMEOUT);

      InputStream in = connection.getInputStream();
      Files.copy(in, Paths.get(target), StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      logger.error("Exception Occurred: ", e);
      return false;
    }

    return true;
  }

  public static String buildExample(List<String> examples) {

    StringBuilder htmlBuilder = new StringBuilder();
    htmlBuilder.append("<div class=\"content-container\">");
    htmlBuilder.append("<ul class=\"content-circle\">");
    for (String example : examples) {
      htmlBuilder.append("<li class=\"content-example\">" + example + "</li>");
    }
    htmlBuilder.append("</ul>");
    htmlBuilder.append("</div>");

    return htmlBuilder.toString();
  }

  public static String buildMeaning(String word, String type, String phonetic, List<Meaning> meanings) {

    return buildMeaning(word, type, phonetic, meanings, false);
  }

  public static String buildMeaning(String word, String type, String phonetic, List<Meaning> meanings,
      boolean isJapanese) {

    StringBuilder htmlBuilder = new StringBuilder();
    if (isJapanese) {
      htmlBuilder.append("<div class=\"content-container japan-font\">");
    } else {
      htmlBuilder.append("<div class=\"content-container\">");
    }
    htmlBuilder.append("<h2 class=\"h\">" + word + "</h2>");
    htmlBuilder.append("<span class=\"content-type\">" + type + "</span>");
    htmlBuilder.append("<span class=\"content-phonetic\">" + phonetic + "</span>");
    htmlBuilder.append("<ol class=\"content-order\">");
    for (Meaning meaning : meanings) {
      if (meaning.getWordType() != null && !meaning.getWordType().isEmpty()) {
        htmlBuilder.append("<h3 class=\"content-type\">" + meaning.getWordType() + "</h3>");
      }

      if (meaning.getMeaning() != null && !meaning.getMeaning().isEmpty()) {
        htmlBuilder.append("<li class=\"content-meaning\">" + meaning.getMeaning() + "</li>");
      }

      if (meaning.getExamples() != null && meaning.getExamples().size() > 0) {
        htmlBuilder.append("<ul class=\"content-circle\">");
        for (String example : meaning.getExamples()) {
          htmlBuilder.append("<li class=\"content-example\">" + example + "</li>");
        }
        htmlBuilder.append("</ul>");
      }
    }
    htmlBuilder.append("</ol>");
    htmlBuilder.append("</div>");

    return htmlBuilder.toString();
  }

  public static Document getJDictDoc(String url, String body) {

    logger.info("body={}", body);
    Document document;
    if (!TheFlashProperties.PROXY_ADDRESS.isEmpty() && TheFlashProperties.PROXY_PORT != 0) {
      Proxy proxy = new Proxy(Type.HTTP,
          new InetSocketAddress(TheFlashProperties.PROXY_ADDRESS, TheFlashProperties.PROXY_PORT));
      JsonObject json = JsonHelper.postRequest(url, body, proxy);
      document = Jsoup.parse(json.get("Content").getAsString());
    } else {
      JsonObject json = JsonHelper.postRequest(url, body);
      document = Jsoup.parse(json.get("Content").getAsString());
    }
    return document;
  }
}
