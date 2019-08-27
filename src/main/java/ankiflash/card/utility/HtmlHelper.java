package ankiflash.card.utility;

import ankiflash.card.dto.Meaning;
import ankiflash.utility.AnkiFlashProps;
import ankiflash.utility.exception.ErrorHandler;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HtmlHelper {

  private static final Logger logger = LoggerFactory.getLogger(HtmlHelper.class);

  public static String lookupUrl(String dictUrl, String word) {
    word = word.replaceAll(" ", "%20");
    return String.format(dictUrl, word);
  }

  public static String decodeValue(String value) {
    try {
      return URLDecoder.decode(value, StandardCharsets.UTF_8.toString());
    } catch (UnsupportedEncodingException e) {
      ErrorHandler.log(e);
    }
    return "";
  }

  public static Document getDocument(String url) {

    Document document = null;
    try {
      if (!AnkiFlashProps.PROXY_ADDRESS.isEmpty() && AnkiFlashProps.PROXY_PORT != 0) {
        Proxy proxy =
            new Proxy(
                Type.HTTP,
                new InetSocketAddress(AnkiFlashProps.PROXY_ADDRESS, AnkiFlashProps.PROXY_PORT));
        document = Jsoup.connect(url).proxy(proxy).get();
      } else {
        document = Jsoup.connect(url).get();
      }
    } catch (IOException e) {
      ErrorHandler.log(e);
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

  public static String buildExample(List<String> examples) {

    return buildExample(examples, false);
  }

  public static String buildExample(List<String> examples, boolean isJapanese) {

    StringBuilder htmlBuilder = new StringBuilder();
    if (isJapanese) {
      htmlBuilder.append("<div class=\"content-container japan-font\">");
    } else {
      htmlBuilder.append("<div class=\"content-container\">");
    }
    htmlBuilder.append("<ul class=\"content-circle\">");
    for (String example : examples) {
      htmlBuilder.append(String.format("<li class=\"content-example\">%s</li>", example));
    }
    htmlBuilder.append("</ul>");
    htmlBuilder.append("</div>");

    return htmlBuilder.toString();
  }

  public static String buildMeaning(
      String word, String type, String phonetic, List<Meaning> meanings) {

    return buildMeaning(word, type, phonetic, meanings, false);
  }

  public static String buildMeaning(
      String word, String type, String phonetic, List<Meaning> meanings, boolean isJapanese) {

    StringBuilder htmlBuilder = new StringBuilder();
    if (isJapanese) {
      htmlBuilder.append("<div class=\"content-container japan-font\">");
    } else {
      htmlBuilder.append("<div class=\"content-container\">");
    }

    htmlBuilder.append(String.format("<h2 class=\"h\">%s</h2>", word));
    if (type != null && !type.isEmpty()) {
      htmlBuilder.append(String.format("<span class=\"content-type\">%s</span>", type));
    }

    if (phonetic != null && !phonetic.isEmpty()) {
      htmlBuilder.append(String.format("<span class=\"content-phonetic\">%s</span>", phonetic));
    }

    htmlBuilder.append("<ol class=\"content-order\">");
    for (Meaning meaning : meanings) {
      if (meaning.getWordType() != null && !meaning.getWordType().isEmpty()) {
        htmlBuilder.append(
            String.format("<h3 class=\"content-type\">%s</h3>", meaning.getWordType()));
      }

      if (meaning.getMeaning() != null && !meaning.getMeaning().isEmpty()) {
        htmlBuilder.append(
            String.format("<li class=\"content-meaning\">%s</li>", meaning.getMeaning()));
      }

      if (meaning.getExamples() != null && !meaning.getExamples().isEmpty()) {
        htmlBuilder.append("<ul class=\"content-circle\">");

        for (String example : meaning.getExamples()) {
          htmlBuilder.append(String.format("<li class=\"content-example\">%s</li>", example));
        }
        htmlBuilder.append("</ul>");
      }
    }
    htmlBuilder.append("</ol>");
    htmlBuilder.append("</div>");

    return htmlBuilder.toString();
  }
}
