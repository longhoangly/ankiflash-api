package ankiflash.card.service.impl.dictionary;

import ankiflash.card.dto.Meaning;
import ankiflash.card.utility.Constants;
import ankiflash.card.utility.DictHelper;
import ankiflash.card.utility.HtmlHelper;
import ankiflash.card.utility.Translation;
import ankiflash.utility.exception.BadRequestException;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JishoDictionaryServiceImpl extends DictionaryServiceImpl {

  private static final Logger logger = LoggerFactory.getLogger(JishoDictionaryServiceImpl.class);

  @Override
  public boolean isConnectionFailed(String combinedWord, Translation translation) {

    String[] wordParts = combinedWord.split(":");
    if (combinedWord.contains(":") && wordParts.length == 3) {
      this.word = wordParts[0];
      this.wordId = wordParts[1];
      this.originalWord = wordParts[2];
    } else {
      throw new BadRequestException("Incorrect word format: " + combinedWord);
    }

    String url = HtmlHelper.lookupUrl(Constants.JISHO_WORD_URL_JP_EN, this.wordId);
    doc = HtmlHelper.getDocument(url);
    return doc == null;
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
  public void preProceedImage(String ankiDir, String selector) {

    this.ankiDir = ankiDir;
    imageLink = imageName = "";
    imageOffline =
        imageOnline =
            "<a href=\"https://www.google.com/search?biw=1280&bih=661&tbm=isch&sa=1&q="
                + word
                + "\" style=\"font-size: 15px; color: blue\">Example Images</a>";
  }

  @Override
  public void preProceedSound(String ankiDir, String selector) {

    this.ankiDir = ankiDir;
    soundLink = HtmlHelper.getAttribute(doc, selector, 0, "src");
    if (soundLink.isEmpty()) {
      soundName = soundLink = soundOnline = soundOffline = "";
      return;
    }

    soundLink = "https:" + soundLink;
    soundName = DictHelper.getLastElement(soundLink);
    soundOnline = String.format("<source src=\"%1$s\">Online sound. %2$s", soundLink, soundLink);
    soundOffline = String.format("<source src=\"%1$s\">Offline sound. %2$s", soundName, soundName);
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

  private List<String> getJishoJapaneseSentences(String word) {

    String url = HtmlHelper.lookupUrl(Constants.JISHO_SEARCH_URL_JP_EN, word + "%20%23sentences");
    Document document = HtmlHelper.getDocument(url);

    List<String> sentences = new ArrayList<>();
    Elements sentenceElms = new Elements();
    if (document != null) {
      sentenceElms = document.select(".sentence_content");
    }

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
