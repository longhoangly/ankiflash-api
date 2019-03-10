package theflash.flashcard.service.impl.dictionary;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import theflash.flashcard.utils.Constants;
import theflash.flashcard.utils.HtmlHelper;
import theflash.flashcard.utils.Meaning;
import theflash.flashcard.utils.Translation;
import theflash.utility.TheFlashProperties;

public class OxfordDictionaryServiceImpl extends DictionaryServiceImpl {

  private static final Logger logger = LoggerFactory.getLogger(OxfordDictionaryServiceImpl.class);

  @Override
  public boolean isConnectionEstablished(String word, Translation translation) {
    this.word = word;
    this.translation = translation;
    boolean isConnectionEstablished = false;
    String url = HtmlHelper.lookupUrl(Constants.DICT_OXFORD_URL_EN_EN, word);
    doc = HtmlHelper.getDocument(url);
    if (doc != null) {
      isConnectionEstablished = true;
    }
    return isConnectionEstablished;
  }

  @Override
  public boolean isWordingCorrect() {
    boolean isWordingCorrect = true;
    String title = HtmlHelper.getText(doc, "title", 0);
    if (title.contains(Constants.DICT_OXFORD_SPELLING_WRONG_1) ||
        title.contains(Constants.DICT_OXFORD_SPELLING_WRONG_2)) {
      isWordingCorrect = false;
    }
    String word = HtmlHelper.getText(doc, "h2", 0);
    if (word.isEmpty()) {
      isWordingCorrect = false;
    }
    return isWordingCorrect;
  }

  @Override
  public String getWordType() {
    type = HtmlHelper.getText(doc, "span[class=pos]", 0);
    return type.isEmpty() ? "" : "(" + type + ")";
  }

  @Override
  public String getExample() {

    List<String> examples = new ArrayList<>();
    for (int i = 0; i < 4; i++) {
      String example = HtmlHelper.getText(doc, "span[class=x]", i);
      if (example.isEmpty() && i == 0) {
        return Constants.DICT_NO_EXAMPLE;
      } else if (example.isEmpty()) {
        break;
      } else {
        example = example.replaceAll(word, "{{c1::" + word + "}}");
        examples.add(example);
      }
    }

    return HtmlHelper.buildExample(examples);
  }

  @Override
  public String getPhonetic() {
    String phoneticBrE = HtmlHelper.getText(doc, "span[class=phon]", 0);
    String phoneticNAmE = HtmlHelper.getText(doc, "span[class=phon]", 1);
    phonetic = String.format("%1$s %2$s", phoneticBrE, phoneticNAmE);
    return phonetic.replaceAll("//", " / ");
  }

  @Override
  public String getImage(String username, String selector) {

    String img_link = HtmlHelper.getAttribute(doc, selector, 0, "href");
    if (img_link.isEmpty()) {
      return "<a href=\"https://www.google.com.vn/search?biw=1280&bih=661&tbm=isch&sa=1&q=" + word
          + "\" style=\"font-size: 15px; color: blue\">Search images by the word.</a>";
    }

    String[] img_link_els = img_link.split("/");
    String img_name = img_link_els[img_link_els.length - 1];

    File dir = new File(Paths.get(username, TheFlashProperties.ANKI_DIR_FLASHCARDS).toString());
    if (dir.exists()) {
      String output = Paths.get(username, TheFlashProperties.ANKI_DIR_FLASHCARDS, img_name).toString();
      HtmlHelper.download(img_link, output);
    }

    return "<img src=\"" + img_name + "\"/>";
  }

  @Override
  public String getPron(String username, String selector) {

    String pro_link = HtmlHelper.getAttribute(doc, selector, 0, "data-src-mp3");
    if (pro_link.isEmpty()) {
      return "";
    }

    String[] pro_link_els = pro_link.split("/");
    String pro_name = pro_link_els[pro_link_els.length - 1];

    File dir = new File(Paths.get(username, TheFlashProperties.ANKI_DIR_FLASHCARDS).toString());
    if (dir.exists()) {
      String output = Paths.get(username, TheFlashProperties.ANKI_DIR_FLASHCARDS, pro_name).toString();
      HtmlHelper.download(pro_link, output);
    }

    return "[sound:" + pro_name + "]";
  }

  @Override
  public String getMeaning() {

    List<Element> meanGroup = HtmlHelper.getElements(doc, "li[class=sn-g]");
    List<Meaning> meanings = new ArrayList<>();
    for (Element meanElem : meanGroup) {
      Element defElem = meanElem.selectFirst("span.def");
      Element siblingElem = defElem.nextElementSibling();
      List<String> examples = new ArrayList<>();
      if (siblingElem != null) {
        List<Element> exampleElements = siblingElem.select("span.x");
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