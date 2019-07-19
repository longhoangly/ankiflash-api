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
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OxfordDictionaryServiceImpl extends DictionaryServiceImpl {

  private static final Logger logger = LoggerFactory.getLogger(OxfordDictionaryServiceImpl.class);

  @Override
  public boolean isConnectionFailed(String word, Translation translation) {

    this.word = word;

    boolean isConnectionFailed = true;
    String url = HtmlHelper.lookupUrl(Constants.OXFORD_URL_EN_EN, word);
    doc = HtmlHelper.getDocument(url);
    if (doc != null) {
      isConnectionFailed = false;
    }
    return isConnectionFailed;
  }

  @Override
  public boolean isWordNotFound() {

    String title = HtmlHelper.getText(doc, "title", 0);
    if (title.contains(Constants.OXFORD_SPELLING_WRONG) ||
        title.contains(Constants.OXFORD_WORD_NOT_FOUND)) {
      return true;
    }

    String word = HtmlHelper.getText(doc, "h2", 0);
    return word.isEmpty();
  }

  @Override
  public String getWordType() {

    if (type == null) {
      type = HtmlHelper.getText(doc, "span.pos", 0);

      type = type.isEmpty() ? "" : "(" + type + ")";
    }
    return type;
  }

  @Override
  public String getExample() {

    List<String> examples = new ArrayList<>();
    for (int i = 0; i < 4; i++) {
      String example = HtmlHelper.getText(doc, "span.x", i);
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
      String phoneticBrE = HtmlHelper.getText(doc, "span.phon", 0);
      String phoneticNAmE = HtmlHelper.getText(doc, "span.phon", 1);
      phonetic = String.format("%1$s %2$s", phoneticBrE, phoneticNAmE).replaceAll("//", " / ");
    }
    return phonetic;
  }

  @Override
  public String getImage(String username, String selector) {

    String google_image = "<a href=\"https://www.google.com/search?biw=1280&bih=661&tbm=isch&sa=1&q=" + word
        + "\" style=\"font-size: 15px; color: blue\">Search images by the word.</a>";

    String img_link = HtmlHelper.getAttribute(doc, selector, 0, "href");
    if (img_link.isEmpty()) {
      return google_image;
    }

    String[] img_link_els = img_link.split("/");
    String img_name = img_link_els[img_link_els.length - 1];

    boolean isSuccess = false;
    File dir = new File(Paths.get(username, AnkiFlashProps.ANKI_DIR_FLASHCARDS).toString());
    if (dir.exists()) {
      String output = Paths.get(username, AnkiFlashProps.ANKI_DIR_FLASHCARDS, img_name).toString();
      isSuccess = IOUtility.download(img_link, output);
    }

    return isSuccess ? "<img src=\"" + img_name + "\"/>" : google_image;
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
    File dir = new File(Paths.get(username, AnkiFlashProps.ANKI_DIR_FLASHCARDS).toString());
    if (dir.exists()) {
      String output = Paths.get(username, AnkiFlashProps.ANKI_DIR_FLASHCARDS, pro_name).toString();
      isSuccess = IOUtility.download(pro_link, output);
    }

    return isSuccess ? "[sound:" + pro_name + "]" : "";
  }

  @Override
  public String getMeaning() {

    getWordType();
    getPhonetic();

    List<Meaning> meanings = new ArrayList<>();
    Elements meanGroup = doc.select("li.sn-g");
    for (Element meanElem : meanGroup) {
      Element defElem = meanElem.selectFirst("span.def");

      List<String> examples = new ArrayList<>();
      Element siblingElem = defElem.nextElementSibling();
      if (siblingElem != null) {
        Elements exampleElements = siblingElem.select("span.x");
        for (Element exampleElem : exampleElements) {
          examples.add(exampleElem.text());
        }
      }

      Meaning meaning = new Meaning(defElem.text(), examples);
      meanings.add(meaning);
    }

    return HtmlHelper.buildMeaning(word, type, phonetic, meanings);
  }

  @Override
  public String getDictionaryName() {
    return "Oxford Advanced Learner's Dictionary";
  }
}