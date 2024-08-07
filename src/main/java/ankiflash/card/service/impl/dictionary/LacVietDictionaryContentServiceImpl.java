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
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LacVietDictionaryContentServiceImpl extends DictionaryContentServiceImpl {

  private static final Logger logger =
      LoggerFactory.getLogger(LacVietDictionaryContentServiceImpl.class);

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

    String url = "";
    if (translation.equals(Translation.VN_EN)) {
      url = HtmlHelper.lookupUrl(Constant.LACVIET_URL_VN_EN, this.wordId);
    } else if (translation.equals(Translation.VN_FR)) {
      url = HtmlHelper.lookupUrl(Constant.LACVIET_URL_VN_FR, this.wordId);
    } else if (translation.equals(Translation.EN_VN)) {
      url = HtmlHelper.lookupUrl(Constant.LACVIET_URL_EN_VN, this.wordId);
    } else if (translation.equals(Translation.FR_VN)) {
      url = HtmlHelper.lookupUrl(Constant.LACVIET_URL_FR_VN, this.wordId);
    }
    doc = HtmlHelper.getDocument(url);
    return doc == null;
  }

  @Override
  public boolean isInvalidWord() {

    Elements words = doc.select("div.w.fl");
    if (words.isEmpty()) {
      return true;
    }

    String warning = HtmlHelper.getText(doc, "div.i.p10", 0);
    return warning.contains(Constant.LACVIET_SPELLING_WRONG);
  }

  @Override
  public String getWordType() {

    if (type == null) {
      Element element = HtmlHelper.getElement(doc, "div.m5t.p10lr", 0);
      type =
          element != null ? element.text().replace("|Tất cả", "").replace("|Từ liên quan", "") : "";

      if (type.isEmpty()) {
        List<String> elements = HtmlHelper.getTexts(doc, "div.m5t.p10lr");
        type = elements.size() > 0 ? String.join(" | ", elements) : "";
      }

      type = type.isEmpty() ? "" : "(" + type + ")";
    }
    return type;
  }

  @Override
  public String getExample() {

    List<String> examples = new ArrayList<>();
    for (int i = 0; i < 4; i++) {
      String example = HtmlHelper.getText(doc, "div.e", i);
      if (example.isEmpty() && i == 0) {
        return Constant.NO_EXAMPLE;
      } else if (example.isEmpty()) {
        break;
      } else {
        word = word.toLowerCase();
        example = example.toLowerCase();
        if (example.contains(word)) {
          example = example.replaceAll(word, "{{c1::" + word + "}}");
        } else {
          example = String.format("%s %s", example, "{{c1::...}}");
        }
        examples.add(example);
      }
    }

    return HtmlHelper.buildExample(examples);
  }

  @Override
  public String getPhonetic() {

    if (phonetic == null) {
      phonetic = HtmlHelper.getText(doc, "div.p5l.fl.cB", 0);
    }
    return phonetic;
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
    soundLinks = HtmlHelper.getAttribute(doc, "embed", 0, "flashvars");
    if (soundLinks.isEmpty()) {
      soundLinks = soundOnline = soundOffline = "";
      return;
    }

    soundLinks = soundLinks.replace("file=", "").replace("&autostart=false", "");
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
    Elements meanGroups = doc.select("div[id*=partofspeech]");

    for (Element meanGroup : meanGroups) {
      Elements meanElms = meanGroup.getElementsByTag("div");
      int meanCount = meanGroup.getElementsByClass("m").size();
      if (meanGroup.attr("id").equalsIgnoreCase("partofspeech_100")) {
        meanElms.addAll(meanGroup.getElementsByTag("a"));
      }

      Meaning meaning = new Meaning();
      List<String> examples = new ArrayList<>();
      boolean firstMeaning = true;

      for (Element meanElm : meanElms) {
        if (meanElm.hasClass("ub")) {
          if (meanCount > 0) {
            // has meaning => get text
            meaning.setWordType(meanElm.text());
          } else {
            // only type => get inner html
            meaning.setWordType(meanElm.html().replaceAll("\n", ""));
          }
        } else if (meanElm.hasClass("m")) {
          // from the second meaning tag
          if (!firstMeaning) {
            meaning.setExamples(examples);
            meanings.add(meaning);
            // reset values
            meaning = new Meaning();
            examples = new ArrayList<>();
          }

          meaning.setMeaning(meanElm.text());
          firstMeaning = false;
        } else if (meanElm.hasClass("e")
            || meanElm.hasClass("em")
            || meanElm.hasClass("im")
            || meanElm.hasClass("id")
            || meanElm.hasAttr("href")) {
          examples.add(meanElm.text());
        }
      }

      meaning.setExamples(examples);
      meanings.add(meaning);
    }

    return HtmlHelper.buildMeaning(word, type, phonetic, meanings);
  }

  @Override
  public String getDictionaryName() {
    return "Lac Viet Dictionary";
  }
}
