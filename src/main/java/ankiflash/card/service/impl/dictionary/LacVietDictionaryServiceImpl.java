package ankiflash.card.service.impl.dictionary;

import ankiflash.card.dto.Meaning;
import ankiflash.card.utility.Constants;
import ankiflash.card.utility.HtmlHelper;
import ankiflash.card.utility.Translation;
import ankiflash.utility.IOUtility;
import ankiflash.utility.TheFlashProperties;
import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LacVietDictionaryServiceImpl extends DictionaryServiceImpl {

  private static final Logger logger = LoggerFactory.getLogger(LacVietDictionaryServiceImpl.class);

  @Override
  public boolean isConnectionEstablished(String word, Translation translation) {

    this.word = word;

    String url = "";
    boolean isConnectionEstablished = false;
    if (translation.equals(Translation.VN_EN)) {
      url = HtmlHelper.lookupUrl(Constants.DICT_LACVIET_URL_VN_EN, word);
    } else if (translation.equals(Translation.VN_FR)) {
      url = HtmlHelper.lookupUrl(Constants.DICT_LACVIET_URL_VN_FR, word);
    } else if (translation.equals(Translation.EN_VN)) {
      url = HtmlHelper.lookupUrl(Constants.DICT_LACVIET_URL_EN_VN, word);
    } else if (translation.equals(Translation.FR_VN)) {
      url = HtmlHelper.lookupUrl(Constants.DICT_LACVIET_URL_FR_VN, word);
    }
    doc = HtmlHelper.getDocument(url);
    if (doc != null) {
      isConnectionEstablished = true;
    }
    return isConnectionEstablished;
  }

  @Override
  public boolean isWordingCorrect() {

    Elements words = doc.select("div.w.fl");
    if (words.isEmpty()) {
      return false;
    }

    String warning = HtmlHelper.getText(doc, "div.i.p10", 0);
    if (warning.contains(Constants.DICT_LACVIET_SPELLING_WRONG)) {
      return false;
    }

    return true;
  }

  @Override
  public String getWordType() {

    if (type == null) {
      Element element = HtmlHelper.getElement(doc, "div.m5t.p10lr", 0);
      type = element != null ? element.text().replace("|Tất cả", "").replace("|Từ liên quan", "") : "";

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
      phonetic = HtmlHelper.getText(doc, "div.p5l.fl.cB", 0);
    }
    return phonetic;
  }

  @Override
  public String getImage(String username, String selector) {

    return "<a href=\"https://www.google.com/search?biw=1280&bih=661&tbm=isch&sa=1&q=" + word
        + "\" style=\"font-size: 15px; color: blue\">Example Images</a>";
  }

  @Override
  public String getPron(String username, String selector) {

    String pro_link = HtmlHelper.getAttribute(doc, selector, 0, "flashvars");
    if (pro_link.isEmpty()) {
      return "";
    }

    pro_link = pro_link.replace("file=", "").replace("&autostart=false", "");
    String[] pro_link_els = pro_link.split("/");
    String pro_name = pro_link_els[pro_link_els.length - 1];

    boolean isSuccess = false;
    File dir = new File(Paths.get(username, TheFlashProperties.ANKI_DIR_FLASHCARDS).toString());
    if (dir.exists()) {
      String output = Paths.get(username, TheFlashProperties.ANKI_DIR_FLASHCARDS, pro_name).toString();
      isSuccess = IOUtility.download(pro_link, output);
    }

    return isSuccess ? "[sound:" + pro_name + "]" : "";
  }

  @Override
  public String getMeaning() {

    getWordType();
    getPhonetic();

    List<Meaning> meanings = new ArrayList<>();
    Elements meanGroups = doc.select("div[id*=partofspeech]");

    for (Element meanGroup : meanGroups) {
      Elements meanElements = meanGroup.getElementsByTag("div");
      int meanCount = meanGroup.getElementsByClass("m").size();

      Meaning meaning = new Meaning();
      List<String> examples = new ArrayList<>();
      boolean firstMeaning = true;

      for (Element meanElem : meanElements) {
        if (meanElem.hasClass("ub")) {
          if (meanCount > 0) {
            // has meaning => get text
            meaning.setWordType(meanElem.text());
          } else {
            // only type => get inner html
            meaning.setWordType(meanElem.html());
          }
        } else if (meanElem.hasClass("m")) {
          // from the second meaning tag
          if (!firstMeaning) {
            meaning.setExamples(examples);
            meanings.add(meaning);
            // reset values
            meaning = new Meaning();
            examples = new ArrayList<>();
          }

          meaning.setMeaning(meanElem.text());
          firstMeaning = false;
        } else if (meanElem.hasClass("e") || meanElem.hasClass("em") ||
            meanElem.hasClass("im") || meanElem.hasClass("id")) {
          examples.add(meanElem.text());
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
