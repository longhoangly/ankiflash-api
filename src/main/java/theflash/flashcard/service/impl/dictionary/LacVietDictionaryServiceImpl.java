package theflash.flashcard.service.impl.dictionary;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.nodes.Element;
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

    boolean isWordingCorrect = true;
    String word = HtmlHelper.getText(doc, "div[class=w fl]", 0);
    if (word.isEmpty()) {
      isWordingCorrect = false;
    }

    String lacResult = HtmlHelper.getText(doc, "div[class=i p10]", 0);
    if (lacResult.contains(Constants.DICT_LACVIET_SPELLING_WRONG)) {
      isWordingCorrect = false;
    }
    return isWordingCorrect;
  }

  @Override
  public String getWordType() {

    if (type == null) {
      List<String> types = new ArrayList<>();
      List<Element> elements = HtmlHelper.getElements(doc, "div[class=ub]");
      for (Element elem : elements) {
        String text = elem.text();
        if (!text.equalsIgnoreCase("Từ liên quan")) {
          types.add(text);
        }
      }
      type = "(" + String.join(" / ", types) + ")";
    }
    return type;
  }

  @Override
  public String getExample() {

    List<String> examples = new ArrayList<>();
    for (int i = 0; i < 4; i++) {
      String example = HtmlHelper.getText(doc, "div[class=e]", i);
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

    if (phonetic == null) {
      phonetic = HtmlHelper.getText(doc, "div[class=p5l fl cB]", 0);
    }
    return phonetic;
  }

  @Override
  public String getImage(String username, String selector) {

    return "<a href=\"https://www.google.com.vn/search?biw=1280&bih=661&tbm=isch&sa=1&q=" + word
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
    List<String> examples = new ArrayList<>();
    Meaning meaning = new Meaning();
    boolean processMeaning = false;

    List<Element> meanGroups = HtmlHelper.getElements(doc, "div[id*=partofspeech]");
    for (Element meanGroup : meanGroups) {
      if (!meanGroup.attr("id").equalsIgnoreCase("partofspeech_100")) {
        List<Element> meanElements = meanGroup.getElementsByTag("div");
        for (Element meanElem : meanElements) {
          if (meanElem.hasClass("ub")) {
            meaning.setWordType(meanElem.text());
          } else if (meanElem.hasClass("m")) {
            if (processMeaning) {
              meaning.setExamples(examples);
              meanings.add(meaning);
              // reset values
              meaning = new Meaning();
              examples = new ArrayList<>();
            }
            processMeaning = true;
            meaning.setMeaning(meanElem.text());
          } else if (meanElem.hasClass("e") || meanElem.hasClass("em") ||
              meanElem.hasClass("im") || meanElem.hasClass("id")) {
            examples.add(meanElem.text());
          }
        }
        meaning.setExamples(examples);
        meanings.add(meaning);
      }
    }

    return HtmlHelper.buildMeaning(word, type, phonetic, meanings);
  }

  @Override
  public String getDictionaryName() {
    return "Lac Viet Dictionary";
  }
}
