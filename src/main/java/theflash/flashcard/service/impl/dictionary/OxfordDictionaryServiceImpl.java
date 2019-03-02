package theflash.flashcard.service.impl.dictionary;

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
import theflash.utility.IOUtility;

public class OxfordDictionaryServiceImpl extends DictionaryServiceImpl {

  private static final Logger logger = LoggerFactory.getLogger(OxfordDictionaryServiceImpl.class);

  @Override
  public List<Translation> supportedTranslations() {
    List<Translation> translations = new ArrayList<>();
    translations.add(new Translation(Constants.ENGLISH, Constants.ENGLISH));
    return translations;
  }

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
    String type = HtmlHelper.getText(doc, "span[class=pos]", 0);
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
    String phoneticBrE = HtmlHelper.getInnerHtml(doc, "span[class=phon]", 0);
    String phoneticNAmE = HtmlHelper.getInnerHtml(doc, "span[class=phon]", 1);
    String phonetic = String.format("%s %s", phoneticBrE, phoneticNAmE);
    phonetic = phonetic.replace("<span class=\"separator\">/</span>", "");
    phonetic = phonetic
        .replace("<span class=\"bre\">BrE</span><span class=\"wrap\">/</span>", "BrE /");
    phonetic = phonetic.replace(
        "<span class=\"wrap\">/</span><span class=\"name\">NAmE</span><span class=\"wrap\">/</span>",
        "/  &nbsp; NAmE /");
    phonetic = String.format("<span class=\"phon\">%s</span>", phonetic);
    return phonetic;
  }

  @Override
  public String getImage(String selector, String attr) {
    String img_link = HtmlHelper.getAttribute(doc, selector, 0, attr);
    if (img_link.isEmpty()) {
      return "<a href=\"https://www.google.com.vn/search?biw=1280&bih=661&tbm=isch&sa=1&q=" + word
          + "\" style=\"font-size: 15px; color: blue\">Search Google Image for the word!</a>";
    }

    String img_name = img_link.split("/")[img_link.split("/").length - 1];
    String output = Paths.get(Constants.ANKI_DIR_IMAGE, img_name).toString();
    IOUtility.createDirs(Constants.ANKI_DIR_IMAGE);
    HtmlHelper.download(img_link, output);
    return "<img src=\"" + img_name + "\"/>";
  }

  @Override
  public String getPron(String selector) {
    String pro_link = HtmlHelper.getAttribute(doc, selector, 0, "data-src-mp3");
    if (pro_link.isEmpty()) {
      return null;
    }
    String pro_name = pro_link.split("/")[pro_link.split("/").length - 1];
    String output = Paths.get(Constants.ANKI_DIR_SOUND, pro_name).toString();
    IOUtility.createDirs(Constants.ANKI_DIR_SOUND);
    HtmlHelper.download(pro_link, output);
    return "[sound:" + pro_name + "]";
  }

  @Override
  public String getMeaning() {

    List<Element> meanElements = HtmlHelper.getElements(doc, "span[class=def]");
    List<Meaning> meanings = new ArrayList<>();
    for (Element meanElement : meanElements) {
      Element sibling = meanElement.nextElementSibling();
      List<String> examples = new ArrayList<>();
      if (sibling != null) {
        List<Element> exampleElements = sibling.select("span.x");
        for (Element exampleElement : exampleElements) {
          examples.add(exampleElement.text());
        }
      }
      Meaning meaning = new Meaning(meanElement.text(), examples);
      meanings.add(meaning);
    }

    return HtmlHelper.buildMeaning(word, getWordType(), getPhonetic(), meanings);
  }

  @Override
  public String getDictionaryName() {
    return "Oxford Advanced Learner's Dictionary";
  }
}