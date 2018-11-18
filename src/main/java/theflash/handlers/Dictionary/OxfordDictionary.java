package theflash.handlers.Dictionary;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.nodes.Element;
import theflash.handlers.utility.Constants;
import theflash.handlers.utility.ContentRoller;
import theflash.handlers.utility.Translation;

public class OxfordDictionary extends BaseDictionary {

  @Override
  public List<Translation> supportedTranslations() {

    List<Translation> pairs = new ArrayList<>();
    pairs.add(new Translation(Constants.ENGLISH, Constants.ENGLISH));

    return pairs;
  }

  @Override
  public boolean isConnectionEstablished(String word, Translation translation) {

    this.word = word;
    this.translation = translation;

    boolean isConnectionEstablished = false;
    String url = ContentRoller.lookupUrl(Constants.DICT_OXFORD_URL_EN_EN, word);
    doc = ContentRoller.getDocument(url);
    if (doc != null) {
      isConnectionEstablished = true;
    }

    return isConnectionEstablished;
  }

  @Override
  public boolean isWordingCorrect() {

    boolean isWordingCorrect = true;
    String title = ContentRoller.getText(doc, "title", 0);
    if (title.contains(Constants.DICT_OXFORD_SPELLING_WRONG_1) ||
        title.contains(Constants.DICT_OXFORD_SPELLING_WRONG_2)) {
      isWordingCorrect = false;
    }

    String word = ContentRoller.getText(doc, "h2", 0);
    if (word.isEmpty()) {
      isWordingCorrect = false;
    }

    return isWordingCorrect;
  }

  @Override
  public String getWordType() {

    String type = ContentRoller.getText(doc, "span[class=pos]", 0);
    return type.isEmpty() ? "" : "(" + type + ")";
  }

  @Override
  public String getExample() {

    Element firstExample = ContentRoller.getElement(doc, "span[class=x-g]", 0);
    if (firstExample == null) {
      return Constants.DICT_NO_EXAMPLE;
    }

    String examples = firstExample.outerHtml();
    for (int i = 1; i < 4; i++) {
      try {
        examples += ContentRoller.getElement(doc, "span[class=x-g]", i).outerHtml();
      } catch (Exception e) {
        e.printStackTrace();
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

    examples = "<link type=\"text/css\" rel=\"stylesheet\" href=\"oxford.css\">" + examples;
    return examples;
  }

  @Override
  public String getPhonetic() {

    String phoneticBrE = ContentRoller.getInnerHtml(doc, "span[class=phon]", 0);
    String phoneticNAmE = ContentRoller.getInnerHtml(doc, "span[class=phon]", 1);
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

    String img_link = ContentRoller.getAttribute(doc, selector, 0, attr);
    if (img_link.isEmpty()) {
      return "<a href=\"https://www.google.com.vn/search?biw=1280&bih=661&tbm=isch&sa=1&q=" + word
          + "\" style=\"font-size: 15px; color: blue\">Search Google Image for the word!</a>";
    }

    String img_name = img_link.split("/")[img_link.split("/").length - 1];
    if (attr.equals("href")) {
      img_name = "fullsize_" + img_name;
    }

    String output = Paths.get(Constants.ANKI_DIR_IMAGE, img_name).toString();
    ContentRoller.download(img_link, output);
    return "<img src=\"" + img_name + "\"/>";
  }

  @Override
  public String getPron(String selector) {
    String pro_link = ContentRoller.getAttribute(doc, selector, 0, "data-src-mp3");
    if (pro_link.isEmpty()) {
      return "";
    }

    String pro_name = pro_link.split("/")[pro_link.split("/").length - 1];
    String output = Paths.get(Constants.ANKI_DIR_SOUND, pro_name).toString();
    ContentRoller.download(pro_link, output);
    return "[sound:" + pro_name + "]";
  }

  @Override
  public String getMeaning() {

    Element element = ContentRoller.getElement(doc, "#entryContent", 0);
    String htmlContent =
        "<html>" + "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">" +
            "<link type=\"text/css\" rel=\"stylesheet\" href=\"interface.css\">" +
            "<link type=\"text/css\" rel=\"stylesheet\" href=\"responsive.css\">" +
            "<link type=\"text/css\" rel=\"stylesheet\" href=\"oxford.css\">" +
            "<div class=\"responsive_entry_center_wrap\">" + element.outerHtml() +
            "</div>" + "</html>";

    htmlContent = htmlContent.replace(Constants.TAB, "");
    htmlContent = htmlContent.replace(Constants.CR, "");
    htmlContent = htmlContent.replace(Constants.LF, "");
    htmlContent = htmlContent.replace("class=\"unbox\"", "class=\"unbox is-active\"");
    return htmlContent;
  }

  @Override
  public String getDictionaryName() {
    return "Oxford Advanced Learner's BaseDictionary";
  }
}