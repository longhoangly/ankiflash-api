package theflash.flashcard.service.impl.dictionary;

import theflash.flashcard.utils.Constants;
import theflash.flashcard.utils.HtmlHelper;
import theflash.flashcard.utils.Translation;

public class CollinsDictionaryServiceImpl extends DictionaryServiceImpl {

  @Override
  public boolean isConnectionEstablished(String word, Translation translation) {

    this.word = word;
    this.translation = translation;

    boolean isConnectionEstablished = false;
    String url = HtmlHelper.lookupUrl(Constants.DICT_CAMBRIDGE_URL_EN_CN, word);
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
    if (title.contains(Constants.DICT_CAMBRIDGE_SPELLING_WRONG)) {
      isWordingCorrect = false;
    }
    String word = HtmlHelper.getText(doc, "span.headword>span", 0);
    if (word.isEmpty()) {
      isWordingCorrect = false;
    }
    return isWordingCorrect;
  }

  @Override
  public String getWordType() {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getExample() {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getPhonetic() {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getImage(String username, String selector) {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getPron(String username, String selector) {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getMeaning() {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getDictionaryName() {
    return "Collins Dictionary";
  }
}
