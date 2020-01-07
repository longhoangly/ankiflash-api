package ankiflash.card.service.impl.dictionary;

import ankiflash.card.dto.Meaning;
import ankiflash.card.utility.Constants;
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

public class CollinsDictionaryServiceImpl extends DictionaryServiceImpl {

  private static final Logger logger = LoggerFactory.getLogger(CollinsDictionaryServiceImpl.class);

  @Override
  public boolean isConnectionFailed(String combinedWord, Translation translation) {

    String[] wordParts = combinedWord.split(Constants.SUB_DELIMITER);
    if (combinedWord.contains(Constants.SUB_DELIMITER) && wordParts.length == 3) {
      this.word = wordParts[0];
      this.wordId = wordParts[1];
      this.originalWord = wordParts[2];
    } else {
      throw new BadRequestException("Incorrect word format: " + combinedWord);
    }

    String url = HtmlHelper.lookupUrl(Constants.COLLINS_URL_FR_EN, this.wordId);
    doc = HtmlHelper.getDocument(url);
    return doc == null;
  }

  @Override
  public boolean isWordNotFound() {

    String mainContent = HtmlHelper.getText(doc, "div.content-box", 0);
    if (mainContent.contains(Constants.COLLINS_SPELLING_WRONG)) {
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
        return Constants.NO_EXAMPLE;
      } else if (example.isEmpty()) {
        break;
      } else {
        word = word.toLowerCase();
        example = example.toLowerCase().replaceAll(word, "{{c1::" + word + "}}");
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
    soundLink = HtmlHelper.getAttribute(doc, selector, 0, "data-src-mp3");
    if (soundLink.isEmpty()) {
      soundLink = soundName = soundOnline = soundOffline = "";
      return;
    }

    soundName = DictHelper.getLastElement(soundLink);
    soundOnline = String.format("<source src=\"%1$s\">Online sound. %2$s", soundLink, soundLink);
    soundOffline = String.format("<source src=\"%1$s\">Offline sound. %2$s", soundName, soundName);
  }

  @Override
  public String getMeaning() {

    getWordType();
    getPhonetic();

    List<Meaning> meanings = new ArrayList<>();
    Elements meanElements = doc.select("div.hom");
    for (Element meanElem : meanElements) {
      Meaning meaning = new Meaning();
      // WordType
      Element type = HtmlHelper.getElement(meanElem, ".pos", 0);
      meaning.setWordType(type != null ? type.text() : "");

      // Meaning
      Elements means = meanElem.select(">div.sense");
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
    Elements examElements = doc.select("div.example_box>blockquote");
    examElements.forEach(e -> examples.add(e.text()));
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
