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

public class CollinsDictionaryContentServiceImpl extends DictionaryContentServiceImpl {

  private static final Logger logger =
      LoggerFactory.getLogger(CollinsDictionaryContentServiceImpl.class);

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

    String url = HtmlHelper.lookupUrl(Constant.COLLINS_URL_FR_EN, this.wordId);
    doc = HtmlHelper.getDocument(url);
    return doc == null;
  }

  @Override
  public boolean isInvalidWord() {

    String mainContent = HtmlHelper.getText(doc, "div.content-box", 0);
    if (mainContent.contains(Constant.COLLINS_SPELLING_WRONG)) {
      return true;
    }

    String word = HtmlHelper.getText(doc, "h2.h2_entry>span", 0);
    return word.isEmpty();
  }

  @Override
  public String getWordType() {

    if (type == null) {
      List<String> texts = HtmlHelper.getTexts(doc, "span.pos");
      type = texts.size() > 0 ? "(" + String.join(" / ", texts) + ")" : "";
    }
    return type;
  }

  @Override
  public String getExample() {

    List<String> examples = new ArrayList<>();
    for (int i = 0; i < 4; i++) {
      String example = HtmlHelper.getText(doc, ".re.type-phr,.cit.type-example", i);
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
      phonetic = HtmlHelper.getText(doc, "span.pron.type-", 0);
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
    soundLinks =
        HtmlHelper.getAttribute(
            doc, "a.hwd_sound.sound.audio_play_button.icon-volume-up.ptr", 0, "data-src-mp3");
    if (soundLinks.isEmpty()) {
      soundLinks = soundOnline = soundOffline = "";
      return;
    }

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
    Elements meanElms = doc.select("div.hom");
    for (Element meanElm : meanElms) {
      Meaning meaning = new Meaning();
      // WordType
      Element type = HtmlHelper.getElement(meanElm, ".pos", 0);
      meaning.setWordType(type != null ? type.text() : "");

      // Meaning
      Elements means = meanElm.select(">div.sense");
      for (Element mean : means) {
        Element re = mean.selectFirst("span[class*=sensenum]");
        if (re != null) {
          re.remove();
        }
        meaning.setMeaning(mean.outerHtml().replaceAll("\n", ""));
        meanings.add(meaning);
        meaning = new Meaning();
      }
    }

    Meaning meaning = new Meaning();
    List<String> examples = new ArrayList<>();
    Elements examElms = doc.select("div.example_box>blockquote");
    examElms.forEach(e -> examples.add(e.text()));
    meaning.setExamples(examples);
    meaning.setWordType("Extra Examples");
    meanings.add(meaning);

    return HtmlHelper.buildMeaning(word, type, phonetic, meanings);
  }

  @Override
  public String getDictionaryName() {
    return "Collins Dictionary";
  }
}
