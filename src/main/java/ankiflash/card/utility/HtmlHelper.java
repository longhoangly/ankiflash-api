package ankiflash.card.utility;

import ankiflash.card.dto.Meaning;
import ankiflash.utility.AnkiFlashProps;
import ankiflash.utility.JsonUtility;
import com.google.gson.JsonObject;
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

  private static String decodeValue(String value) {
    try {
      return URLDecoder.decode(value, StandardCharsets.UTF_8.toString());
    } catch (UnsupportedEncodingException e) {
      logger.error("Exception Occurred: ", e);
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
      logger.error("Exception Occurred: ", e);
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

  /* === Jdict Specific Methods === */

  /**
   * To get Jdict HTML source, we need to send a post request to Jdict
   *
   * @param url post url
   * @param body json input for post request
   * @return Jsoup doucument
   */
  public static Document getJDictDoc(String url, String body) {

    logger.info("body={}", body);
    JsonObject json = JsonUtility.postRequest(url, body);
    return Jsoup.parse(json.get("Content").getAsString());
  }

  public static List<String> getJDictWords(String word, boolean firstOnly) {

    String urlParameters =
        String.format("m=dictionary&fn=search_word&keyword=%1$s&allowSentenceAnalyze=true", word);
    Document document = HtmlHelper.getJDictDoc(Constants.JDICT_URL_VN_JP_OR_JP_VN, urlParameters);
    Elements wordElms = document.select("ul>li");

    List<String> jDictWords = new ArrayList<>();
    for (Element wordElem : wordElms) {
      if (wordElem.attr("title").toLowerCase().contains(word.toLowerCase())
          && !wordElem.attr("data-id").isEmpty()) {
        jDictWords.add(wordElem.attr("title") + ":" + wordElem.attr("data-id") + ":" + word);
      }

      if (firstOnly) {
        break;
      }
    }
    return jDictWords;
  }

  public static String getJDictWord(String word) {
    List<String> words = getJDictWords(word, true);
    return words.isEmpty() ? "" : words.get(0);
  }

  /* === Jisho Specific Methods === */

  public static List<String> getJishoWords(String word, boolean firstOnly) {

    String url = HtmlHelper.lookupUrl(Constants.JISHO_SEARCH_URL_JP_EN, word);
    Document document = HtmlHelper.getDocument(url);

    Elements wordElms = document.select(".concept_light.clearfix");
    List<String> jDictWords = new ArrayList<>();
    for (Element wordElem : wordElms) {

      Element foundWordElm = HtmlHelper.getElement(wordElem, ".concept_light-representation", 0);
      Element detailLink = HtmlHelper.getElement(wordElem, ".light-details_link", 0);

      if (foundWordElm != null
          && foundWordElm.text().toLowerCase().contains(word.toLowerCase())
          && detailLink != null
          && !detailLink.text().isEmpty()) {

        String[] detailLinkEls = detailLink.attr("href").split("/");
        jDictWords.add(
            decodeValue(
                foundWordElm.text() + ":" + detailLinkEls[detailLinkEls.length - 1] + ":" + word));
      }

      if (firstOnly) {
        break;
      }
    }

    return jDictWords;
  }

  public static String getJishoWord(String word) {
    List<String> words = getJishoWords(word, true);
    return words.isEmpty() ? "" : words.get(0);
  }
}
