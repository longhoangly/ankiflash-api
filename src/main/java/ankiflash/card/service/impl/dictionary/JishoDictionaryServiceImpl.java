package ankiflash.card.service.impl.dictionary;

import ankiflash.card.dto.Meaning;
import ankiflash.card.utility.Constants;
import ankiflash.card.utility.HtmlHelper;
import ankiflash.card.utility.Translation;
import ankiflash.utility.AnkiFlashProps;
import ankiflash.utility.IOUtility;
import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JishoDictionaryServiceImpl extends DictionaryServiceImpl {

  private static final Logger logger = LoggerFactory.getLogger(JishoDictionaryServiceImpl.class);

  private String originalWord;

  @Override
  public boolean isConnectionFailed(String word, Translation translation) {

    String[] wordParts = word.split(":");
    this.word = wordParts[0];
    String wordId = wordParts[1];
    this.originalWord = wordParts[2];

    boolean isConnectionFailed = true;
    String url = HtmlHelper.lookupUrl(Constants.JISHO_WORD_URL_JP_EN, wordId);
    doc = HtmlHelper.getDocument(url);
    if (doc != null) {
      isConnectionFailed = false;
    }
    return isConnectionFailed;
  }

  @Override
  public boolean isWordNotFound() {

    if (doc.outerHtml().contains(Constants.JISHO_WORD_NOT_FOUND)) {
      return true;
    }
    String word = HtmlHelper.getText(doc, ".concept_light-representation", 0);
    return word.isEmpty();
  }

  @Override
  public String getWordType() {

    if (type == null) {
      Elements elements = doc.select("div.concept_light.clearfix div.meaning-tags");
      List<String> wordTypes = new ArrayList<>();
      for (Element element : elements) {
        if (element.hasText()
            && !element.text().equals("Wikipedia definition")
            && !element.text().equals("Other forms")) {
          wordTypes.add("[" + element.text() + "]");
        }
      }

      type = wordTypes.isEmpty() ? "" : "(" + String.join(" / ", wordTypes) + ")";
    }

    return type;
  }

  @Override
  public String getExample() {

    List<String> examples = new ArrayList<>();
    for (int i = 0; i < 4; i++) {
      String example = HtmlHelper.getInnerHtml(doc, ".sentence", i);
      if (example.isEmpty() && i == 0) {
        return Constants.NO_EXAMPLE;
      } else if (example.isEmpty()) {
        break;
      } else {
        String lowerWord = this.originalWord.toLowerCase();
        example = example.toLowerCase().replaceAll(lowerWord, "{{c1::" + lowerWord + "}}");
        examples.add(example.replaceAll("\n", ""));
      }
    }

    return HtmlHelper.buildExample(examples, true);
  }

  @Override
  public String getPhonetic() {

    return "";
  }

  @Override
  public String getImage(String username, String sessionId, String selector) {

    return "<a href=\"https://www.google.com/search?biw=1280&bih=661&tbm=isch&sa=1&q="
        + word
        + "\" style=\"font-size: 15px; color: blue\">Example Images</a>";
  }

  @Override
  public String getPron(String username, String sessionId, String selector) {

    String pro_link = HtmlHelper.getAttribute(doc, "audio>source[type=audio/mpeg]", 0, "src");
    if (pro_link.isEmpty()) {
      return "";
    }

    pro_link = "https:" + pro_link;
    String[] pro_link_els = pro_link.split("/");
    String pro_name = pro_link_els[pro_link_els.length - 1];

    boolean isSuccess = false;
    File dir =
        new File(Paths.get(username, sessionId, AnkiFlashProps.ANKI_DIR_FLASHCARDS).toString());
    if (dir.exists()) {
      String output =
          Paths.get(username, sessionId, AnkiFlashProps.ANKI_DIR_FLASHCARDS, pro_name).toString();
      isSuccess = IOUtility.download(pro_link, output);
    } else {
      logger.error("AnkiFlashcards folder not found!");
    }

    return isSuccess ? "[sound:" + pro_name + "]" : "";
  }

  @Override
  public String getMeaning() {

    getWordType();
    getPhonetic();

    List<Meaning> meanings = new ArrayList<>();
    Element meanGroup = HtmlHelper.getElement(doc, ".meanings-wrapper", 0);
    if (meanGroup != null) {

      Meaning meaning;
      Elements meanElements = meanGroup.select(".meaning-tags,.meaning-wrapper");
      for (Element meanElem : meanElements) {

        if (meanElem.hasClass("meaning-tags")) {
          meaning = new Meaning();
          meaning.setWordType(meanElem.text());
          meanings.add(meaning);
        }

        if (meanElem.hasClass("meaning-wrapper")) {
          meaning = new Meaning();
          Element mean = HtmlHelper.getElement(meanElem, ".meaning-meaning", 0);
          if (mean != null) {
            meaning.setMeaning(mean.text());
          }

          List<String> examples = new ArrayList<>();
          Elements exampleElms = meanElem.select(".sentence");
          for (Element exampleElm : exampleElms) {
            if (exampleElm != null) {
              examples.add(exampleElm.html().replaceAll("\n", ""));
            }
          }
          meaning.setExamples(examples);
          meanings.add(meaning);
        }
      }

      meaning = new Meaning();
      List<String> extraExamples = getJishoJapaneseSentences(word);
      if (!extraExamples.isEmpty()) {
        meaning.setWordType("Extra Examples");
        meaning.setExamples(extraExamples);
        meanings.add(meaning);
      }
    }

    return HtmlHelper.buildMeaning(word, type, phonetic, meanings, true);
  }

  @Override
  public String getDictionaryName() {
    return "Jisho Dictionary";
  }

  private static List<String> getJishoJapaneseSentences(String word) {

    String url = HtmlHelper.lookupUrl(Constants.JISHO_SEARCH_URL_JP_EN, word + "%20%23sentences");
    Document document = HtmlHelper.getDocument(url);

    List<String> sentences = new ArrayList<>();
    Elements sentenceElms = document.select(".sentence_content");

    int maxCount = 1;
    for (Element sentenceElm : sentenceElms) {
      sentences.add(sentenceElm.html().replaceAll("\n", ""));

      if (maxCount >= 10) {
        break;
      }
      maxCount++;
    }

    return sentences;
  }
}
