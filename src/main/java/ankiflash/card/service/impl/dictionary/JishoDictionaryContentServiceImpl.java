package ankiflash.card.service.impl.dictionary;

import static java.util.Optional.ofNullable;

import ankiflash.card.dto.Meaning;
import ankiflash.card.utility.Constant;
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

public class JishoDictionaryContentServiceImpl extends DictionaryContentServiceImpl {

  private static final Logger logger =
      LoggerFactory.getLogger(JishoDictionaryContentServiceImpl.class);

  @Override
  public boolean isConnected(String combinedWord, Translation translation) {

    String[] wordParts = combinedWord.split(Constant.SUB_DELIMITER);
    if (combinedWord.contains(Constant.SUB_DELIMITER) && wordParts.length == 3) {
      this.word = wordParts[0];
      this.wordId = wordParts[1];
      this.originalWord = wordParts[2];
    } else {
      throw new BadRequestException("Incorrect word format: " + combinedWord);
    }

    String url = HtmlHelper.lookupUrl(Constant.JISHO_WORD_URL_JP_EN, this.wordId);
    doc = HtmlHelper.getDocument(url);
    return doc == null;
  }

  @Override
  public boolean isInvalidWord() {

    if (doc.outerHtml().contains(Constant.JISHO_WORD_NOT_FOUND)) {
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
        return Constant.NO_EXAMPLE;
      } else if (example.isEmpty()) {
        break;
      } else {
        String lowerWord = this.originalWord.toLowerCase();
        example = example.toLowerCase();
        if (example.contains(lowerWord)) {
          example = example.replaceAll(lowerWord, "{{c1::" + lowerWord + "}}");
        } else {
          example = String.format("%s %s", example, "{{c1::...}}");
        }
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
  public void getImages(String ankiDir, boolean isOffline) {

    this.ankiDir = ankiDir;
    imageLink = "";
    imageOffline =
        imageOnline =
            "<a href=\"https://www.google.com/search?biw=1280&bih=661&tbm=isch&sa=1&q="
                + word
                + "\" style=\"font-size: 15px; color: blue\">Example Images</a>";
  }

  @Override
  public void getSounds(String ankiDir, boolean isOffline) {

    this.ankiDir = ankiDir;
    soundLinks = HtmlHelper.getAttribute(doc, "audio>source[type=audio/mpeg]", 0, "src");
    if (soundLinks.isEmpty()) {
      soundLinks = soundOnline = soundOffline = "";
      return;
    }

    soundLinks = "https:" + soundLinks;
    String[] sounds = soundLinks.split(";");
    for (var soundLink : sounds) {
      String soundName = DictHelper.getFileName(soundLink);
      soundOnline =
          String.format(
              "<audio src=\"%1$s\" type=\"audio/wav\" preload=\"auto\" autoplay autobuffer controls>[sound:%2$s]</audio> %3$s",
              soundLink, soundLink, ofNullable(soundOnline).orElse(""));
      soundOffline =
          String.format(
              "<audio src=\"%1$s\" type=\"audio/wav\" preload=\"auto\" autoplay autobuffer controls>[sound:%2$s]</audio> %3$s",
              soundName, soundName, ofNullable(soundOffline).orElse(""));
    }

    if (isOffline) {
      DictHelper.downloadFiles(ankiDir, soundLinks);
    }
  }

  @Override
  public String getMeaning() {

    getWordType();
    getPhonetic();

    List<Meaning> meanings = new ArrayList<>();
    Element meanGroup = HtmlHelper.getElement(doc, ".meanings-wrapper", 0);
    if (meanGroup != null) {

      Meaning meaning;
      Elements meanElms = meanGroup.select(".meaning-tags,.meaning-wrapper");
      for (Element meanElm : meanElms) {

        if (meanElm.hasClass("meaning-tags")) {
          meaning = new Meaning();
          meaning.setWordType(meanElm.text());
          meanings.add(meaning);
        }

        if (meanElm.hasClass("meaning-wrapper")) {
          meaning = new Meaning();
          Element mean = HtmlHelper.getElement(meanElm, ".meaning-meaning", 0);
          if (mean != null) {
            meaning.setMeaning(mean.text());
          }

          List<String> examples = new ArrayList<>();
          Elements exampleElms = meanElm.select(".sentence");
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

    String url = HtmlHelper.lookupUrl(Constant.JISHO_SEARCH_URL_JP_EN, word + "%20%23sentences");
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
