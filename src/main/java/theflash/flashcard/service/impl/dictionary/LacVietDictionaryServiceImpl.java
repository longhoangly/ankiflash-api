package theflash.flashcard.service.impl.dictionary;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import theflash.flashcard.utils.Constants;
import theflash.flashcard.utils.HtmlHelper;
import theflash.flashcard.utils.Meaning;
import theflash.flashcard.utils.Translation;
import theflash.utility.IOUtility;

public class LacVietDictionaryServiceImpl extends DictionaryServiceImpl {

  @Override
  public boolean isConnectionEstablished(String word, Translation translation) {

    this.word = word;
    this.translation = translation;

    boolean isConnectionEstablished = false;
    String url;
    if (translation.equals(Translation.VN_EN)) {
      url = HtmlHelper.lookupUrl(Constants.DICT_LACVIET_URL_VN_EN, word);
    } else if (translation.equals(Translation.VN_FR)) {
      url = HtmlHelper.lookupUrl(Constants.DICT_LACVIET_URL_VN_FR, word);
    } else if (translation.equals(Translation.EN_VN)) {
      url = HtmlHelper.lookupUrl(Constants.DICT_LACVIET_URL_EN_VN, word);
    } else {
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

    String word = HtmlHelper.getText(doc, "div.w.fl", 0);
    if (word.isEmpty()) {
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
        + "\" style=\"font-size: 15px; color: blue\">Images for this word</a>";
  }

  @Override
  public String getPron(String username, String selector) {

    String pro_link = HtmlHelper.getAttribute(doc, selector, 0, "flashvars");
    if (pro_link.isEmpty()) {
      return "";
    }
    pro_link = pro_link.replace("file=", "").replace("&autostart=false", "");
    String pro_name = pro_link.split("/")[pro_link.split("/").length - 1];
    String output = Paths.get(username, Constants.ANKI_DIR_SOUND, pro_name).toString();
    IOUtility.createDirs(Paths.get(username, Constants.ANKI_DIR_SOUND).toString());
    HtmlHelper.download(pro_link, output);
    return "[sound:" + pro_name + "]";
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
