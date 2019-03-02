package theflash.flashcard.service.impl.dictionary;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import theflash.flashcard.utils.Constants;
import theflash.flashcard.utils.HtmlHelper;
import theflash.flashcard.utils.Translation;
import theflash.utility.IOUtility;

public class LacVietDictionaryServiceImpl extends DictionaryServiceImpl {

  private static final Logger logger = LoggerFactory.getLogger(LacVietDictionaryServiceImpl.class);

  @Override
  public List<Translation> supportedTranslations() {
    List<Translation> pairs = new ArrayList<>();
    pairs.add(new Translation(Constants.VIETNAMESE, Constants.ENGLISH));
    pairs.add(new Translation(Constants.VIETNAMESE, Constants.FRENCH));
    pairs.add(new Translation(Constants.ENGLISH, Constants.VIETNAMESE));
    pairs.add(new Translation(Constants.FRENCH, Constants.VIETNAMESE));
    return pairs;
  }

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
    boolean isWordingCorrect = false;
    String title = HtmlHelper.getText(doc, "title", 0);
    if (title.contains(Constants.DICT_LACVIET_SPELLING_WRONG)) {
      isWordingCorrect = true;
    }
    String word = HtmlHelper.getText(doc, "div[class=w fl]", 0);
    if (word.isEmpty()) {
      isWordingCorrect = true;
    }
    //ToDo: Check which one we use to check correct word??? lacResult or title???
    String lacResult = HtmlHelper.getText(doc, "div[class=i p10]", 0);
    if (lacResult.contains(Constants.DICT_LACVIET_SPELLING_WRONG)) {
      isWordingCorrect = true;
    }
    return isWordingCorrect;
  }

  @Override
  public String getWordType() {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getExample() {
    Element exampleElements = HtmlHelper.getElement(doc, "div[class=e]", 0);
    if (exampleElements == null) {
      return Constants.DICT_NO_EXAMPLE;
    }
    String examples = exampleElements.outerHtml() + "<br>";
    for (int i = 1; i < 2; i++) {
      try {
        examples += HtmlHelper.getElement(doc, "div[class=e]", i).outerHtml() + "<br>";
      } catch (Exception e) {
        logger.error("Exception: ", e);
        break;
      }
    }
    //ToDo: double check if this function work well or not! Replace all words in examples except html values!
    //    Pattern pattern = Pattern.compile(
    //        String.format("({0})([^\\W_]*?[<>/\\]*?[^\\W_]*?[<>/\\]*?)([\\s.])", word.toLowerCase()));
    //    examples = examples.toLowerCase();
    //    Matcher match = pattern.matcher(examples);
    //    if (match.find()) {
    //      examples = match.replaceAll("{{c1::" + word + "}}$2$3");
    //    }
    examples = "<link type=\"text/css\" rel=\"stylesheet\" href=\"home.css\">" + examples;
    return examples;
  }

  @Override
  public String getPhonetic() {
    String phonetic = HtmlHelper.getText(doc, "div[class=p5l fl cB]", 0);
    return phonetic;
  }

  @Override
  public String getImage(String selector, String attr) {
    String img_link = HtmlHelper.getAttribute(doc, selector, 0, attr);
    if (img_link.isEmpty()) {
      return "<a href=\"https://www.google.com.vn/search?biw=1280&bih=661&tbm=isch&sa=1&q=" + word
          + "\" style=\"font-size: 15px; color: blue\">Images for this word</a>";
    }
    String img_name = img_link.split("/")[img_link.split("/").length - 1];
    if (attr.isEmpty()) {
      img_name = "fullsize_" + img_name;
    }
    String output = Paths.get(Constants.ANKI_DIR_IMAGE, img_name).toString();
    IOUtility.createDirs(Constants.ANKI_DIR_IMAGE);
    HtmlHelper.download(img_link, output);
    return "<img src=\"" + img_name + "\"/>";
  }

  @Override
  public String getPron(String selector) {
    String pro_link = HtmlHelper.getAttribute(doc, selector, 0, "flashvars");
    if (pro_link.isEmpty()) {
      return "";
    }
    pro_link = pro_link.replace("file=", "").replace("&autostart=false", "");
    String pro_name = pro_link.split("/")[pro_link.split("/").length - 1];
    String output = Paths.get(Constants.ANKI_DIR_SOUND, pro_name).toString();
    IOUtility.createDirs(Constants.ANKI_DIR_SOUND);
    HtmlHelper.download(pro_link, output);
    return "[sound:" + pro_name + "]";
  }

  @Override
  public String getMeaning() {
    Element domContent = HtmlHelper
        .getElement(doc, "#ctl00_ContentPlaceHolderMain_cnt_dict", 0);
    String htmlContent =
        "<html>" + "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">" +
            "<link type=\"text/css\" rel=\"stylesheet\" href=\"home.css\">" +
            "<link type=\"text/css\" rel=\"stylesheet\" href=\"responsive.css\">" +
            "<div class=\"responsive_entry_center_wrap\">" + domContent.outerHtml() +
            "</div>" + "</html>";
    htmlContent = htmlContent.replace(Constants.TAB, "");
    htmlContent = htmlContent.replace(Constants.CR, "");
    htmlContent = htmlContent.replace(Constants.LF, "");
    htmlContent = htmlContent.replaceAll("<div class=\"p5l fl\".*?</div>", "");
    htmlContent = htmlContent.replaceAll("<div class=\"p3l fl m3t\">.*?</div>", "");
    htmlContent = htmlContent.replaceAll("<div class=\"cgach p5lr fl\">|</div>", "");
    htmlContent = htmlContent
        .replace("<div id=\"firstHeading\"> </div>", "<div id=\"firstHeading\">" + word + "</div>");
    return htmlContent;
  }

  @Override
  public String getDictionaryName() {
    return "Lac Viet Dictionary";
  }
}
