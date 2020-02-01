package ankiflash.card.utility;

import ankiflash.utility.JsonUtility;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CardHelper {

  private static final Logger logger = LoggerFactory.getLogger(CardHelper.class);

  public static Document getJDictDoc(String url, String body) {

    logger.info("body={}", body);
    JsonObject json = JsonUtility.postRequest(url, body);
    return Jsoup.parse(json.get("Content").getAsString());
  }

  public static List<String> getJDictWords(String word) {

    String urlParameters =
        String.format("m=dictionary&fn=search_word&keyword=%1$s&allowSentenceAnalyze=true", word);
    Document document = CardHelper.getJDictDoc(Constants.JDICT_URL_VN_JP_OR_JP_VN, urlParameters);
    Elements wordElms = new Elements();
    if (document != null) {
      wordElms = document.select("ul>li");
    }

    List<String> jDictWords = new ArrayList<>();
    for (Element wordElm : wordElms) {
      if (wordElm.attr("title").toLowerCase().contains(word.toLowerCase())
          && !wordElm.attr("data-id").isEmpty()) {
        jDictWords.add(
            wordElm.attr("title")
                + Constants.SUB_DELIMITER
                + wordElm.attr("data-id")
                + Constants.SUB_DELIMITER
                + word);
      }
    }
    return jDictWords;
  }

  public static List<String> getJishoWords(String word) {

    String url = HtmlHelper.lookupUrl(Constants.JISHO_SEARCH_URL_JP_EN, word);
    Document document = HtmlHelper.getDocument(url);
    Elements wordElms = new Elements();
    if (document != null) {
      wordElms = document.select(".concept_light.clearfix");
    }

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
            HtmlHelper.decodeValue(
                foundWordElm.text()
                    + Constants.SUB_DELIMITER
                    + detailLinkEls[detailLinkEls.length - 1]
                    + Constants.SUB_DELIMITER
                    + word));
      }
    }

    return jDictWords;
  }

  public static List<String> getOxfordWords(String word) {

    List<String> foundWords = new ArrayList<>();
    String url = HtmlHelper.lookupUrl(Constants.OXFORD_SEARCH_URL_EN_EN, word);
    Document doc = HtmlHelper.getDocument(url);

    if (doc != null) {
      String firstLink = HtmlHelper.getAttribute(doc, "link", 0, "href");
      if (firstLink.contains("definition/english")) {
        String matchedWord = HtmlHelper.getText(doc, ".headword", 0);
        if (!matchedWord.isBlank()) {
          foundWords.add(
              matchedWord
                  + Constants.SUB_DELIMITER
                  + DictHelper.getLastElement(firstLink)
                  + Constants.SUB_DELIMITER
                  + word);
        }
      }

      Elements allMatchesBlocks = doc.select("dl.accordion.ui-grad");
      for (Element allMatches : allMatchesBlocks) {
        Elements lis = allMatches.select("li");
        for (Element li : lis) {
          li.getElementsByTag("pos").remove();
          String matchedWord = li.getElementsByTag("span").text().trim();
          if (matchedWord.equalsIgnoreCase(word)) {
            String wordId = DictHelper.getLastElement(li.getElementsByTag("a").attr("href"));
            foundWords.add(
                wordId + Constants.SUB_DELIMITER + wordId + Constants.SUB_DELIMITER + word);
          }
        }
      }
    } else {
      logger.info("Words not found: {}", word);
    }

    return foundWords;
  }
}
