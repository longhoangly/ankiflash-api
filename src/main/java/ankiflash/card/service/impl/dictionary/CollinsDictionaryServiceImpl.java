package ankiflash.card.service.impl.dictionary;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ankiflash.card.utility.Constants;
import ankiflash.card.utility.HtmlHelper;
import ankiflash.card.dto.Meaning;
import ankiflash.card.utility.Translation;
import ankiflash.utility.TheFlashProperties;

public class CollinsDictionaryServiceImpl extends DictionaryServiceImpl {

  private static final Logger logger = LoggerFactory.getLogger(CollinsDictionaryServiceImpl.class);

  @Override
  public boolean isConnectionEstablished(String word, Translation translation) {

    this.word = word;

    boolean isConnectionEstablished = false;
    String url = HtmlHelper.lookupUrl(Constants.DICT_COLLINS_URL_FR_EN, word);
    doc = HtmlHelper.getDocument(url);
    if (doc != null) {
      isConnectionEstablished = true;
    }
    return isConnectionEstablished;
  }

  @Override
  public boolean isWordingCorrect() {

    String mainContent = HtmlHelper.getText(doc, "div.content-box", 0);
    if (mainContent.contains(Constants.DICT_COLLINS_SPELLING_WRONG)) {
      return false;
    }

    String word = HtmlHelper.getText(doc, "h2.h2_entry>span", 0);
    if (word.isEmpty()) {
      return false;
    }

    return true;
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
        return Constants.DICT_NO_EXAMPLE;
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
  public String getImage(String username, String selector) {

    return "<a href=\"https://www.google.com/search?biw=1280&bih=661&tbm=isch&sa=1&q=" + word
        + "\" style=\"font-size: 15px; color: blue\">Images for this word</a>";
  }

  @Override
  public String getPron(String username, String selector) {

    String pro_link = HtmlHelper.getAttribute(doc, selector, 0, "data-src-mp3");
    if (pro_link.isEmpty()) {
      return "";
    }

    String[] pro_link_els = pro_link.split("/");
    String pro_name = pro_link_els[pro_link_els.length - 1];

    boolean isSuccess = false;
    File dir = new File(Paths.get(username, TheFlashProperties.ANKI_DIR_FLASHCARDS).toString());
    if (dir.exists()) {
      String output = Paths.get(username, TheFlashProperties.ANKI_DIR_FLASHCARDS, pro_name).toString();
      isSuccess = HtmlHelper.download(pro_link, output);
    }

    return isSuccess ? "[sound:" + pro_name + "]" : "";
  }

  @Override
  public String getMeaning() {

    getWordType();
    getPhonetic();

    List<Meaning> meanings = new ArrayList<>();
    Elements meanElements = doc.select("div.hom>span.gramGrp,div.hom>div.sense");
    for (Element meanElem : meanElements) {
      Meaning meaning = new Meaning();
      List<String> examples = new ArrayList<>();
      if (meanElem.hasClass("gramGrp")) {
        // WordType
        Element type = HtmlHelper.getElement(meanElem, ".pos", 0);
        meaning.setWordType(type != null ? type.text() : "");
        meanings.add(meaning);
      } else if (meanElem.hasClass("sense")) {
        // Meaning
        List<String> means = new ArrayList<>();
        meanElem.select(">span").forEach(e -> means.add(e.text()));
        String meaningText = String.join(" ", means);
        meaning.setMeaning(meaningText);
        // Examples
        Elements examElements = meanElem.select("div.re.type-phr,div.cit.type-example");
        examElements.forEach(e -> examples.add(e.text()));
        meaning.setExamples(examples);
        meanings.add(meaning);
      }
    }

    Meaning meaning = new Meaning();
    List<String> examples = new ArrayList<>();
    Elements examElements = doc.select("div.example_box>blockquote");
    examElements.forEach(e -> examples.add(e.text()));
    meaning.setExamples(examples);
    meaning.setWordType(HtmlHelper.getText(doc, "div.content-box-header>h2.h2_entry", 1));
    meanings.add(meaning);

    return HtmlHelper.buildMeaning(word, type, phonetic, meanings);
  }

  @Override
  public String getDictionaryName() {
    return "Collins Dictionary";
  }
}
