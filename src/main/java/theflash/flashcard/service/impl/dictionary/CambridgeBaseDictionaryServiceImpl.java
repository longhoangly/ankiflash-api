package theflash.flashcard.service.impl.dictionary;

import java.util.ArrayList;
import java.util.List;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import theflash.flashcard.utils.Constants;
import theflash.flashcard.utils.ContentRoller;
import theflash.flashcard.utils.Translation;

public class CambridgeBaseDictionaryServiceImpl extends BaseDictionaryServiceImpl {

  @Override
  public List<Translation> supportedTranslations() {
    List<Translation> pairs = new ArrayList<>();
    pairs.add(new Translation(Constants.ENGLISH, Constants.CHINESE));
    return pairs;
  }

  @Override
  public boolean isConnectionEstablished(String word, Translation translation) {
    this.word = word;
    this.translation = translation;
    boolean isConnectionEstablished = false;
    String url = ContentRoller.lookupUrl(Constants.DICT_CAMBRIDGE_URL_EN_CN, word);
    doc = ContentRoller.getDocument(url);
    if (doc != null) {
      isConnectionEstablished = true;
    }
    return isConnectionEstablished;
  }

  @Override
  public boolean isWordingCorrect() {
    boolean isWordingCorrect = false;
    String title = ContentRoller.getText(doc, "title", 0);
    if (title.contains(Constants.DICT_CAMBRIDGE_SPELLING_WRONG)) {
      isWordingCorrect = true;
    }
    String word = ContentRoller.getText(doc, "span.headword>span", 0);
    if (word.isEmpty()) {
      isWordingCorrect = true;
    }
    return isWordingCorrect;
  }

  @Override
  public String getWordType() {
    throw new NotImplementedException();
  }

  @Override
  public String getExample() {
    throw new NotImplementedException();
  }

  @Override
  public String getPhonetic() {
    throw new NotImplementedException();
  }

  @Override
  public String getImage(String selector, String attr) {
    throw new NotImplementedException();
  }

  @Override
  public String getPron(String selector) {
    throw new NotImplementedException();
  }

  @Override
  public String getMeaning() {
    Element ukSoundIcon = ContentRoller
        .getElement(doc, "span.circle.circle-btn.sound.audio_play_button.uk", 0);
    if (ukSoundIcon != null) {
      ukSoundIcon.remove();
    }
    Element usSoundIcon = ContentRoller
        .getElement(doc, "span.circle.circle-btn.sound.audio_play_button.us", 0);
    if (usSoundIcon != null) {
      usSoundIcon.remove();
    }
    Element translations = ContentRoller
        .getElement(doc, "div.clrd.mod.mod--style5.mod--dark.mod-translate", 0);
    translations.remove();
    Element shareThisEntry = ContentRoller.getElement(doc, "div.share.rounded.js-share", 0);
    shareThisEntry.remove();
    Elements scripts = ContentRoller.getElements(doc, "script");
    if (scripts.size() > 0) {
      for (Element script : scripts) {
        script.remove();
      }
    }
    Element contentElement = ContentRoller.getElement(doc, "div#entryContent", 0);
    contentElement.addClass("entrybox english-chinese-simplified entry-body");
    String htmlContent =
        "<html>" + "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">" +
            "<link type=\"text/css\" rel=\"stylesheet\" href=\"common.css\">" +
            "<link type=\"text/css\" rel=\"stylesheet\" href=\"responsive.css\">" +
            "<div class=\"responsive_entry_center_wrap\">" + contentElement.outerHtml() +
            "</div>" + "</html>";
    htmlContent = htmlContent.replace(Constants.TAB, "");
    htmlContent = htmlContent.replace(Constants.CR, "");
    htmlContent = htmlContent.replace(Constants.LF, "");
    return htmlContent;
  }

  @Override
  public String getDictionaryName() {
    return "Cambridge BaseDictionaryServiceImpl";
  }
}
