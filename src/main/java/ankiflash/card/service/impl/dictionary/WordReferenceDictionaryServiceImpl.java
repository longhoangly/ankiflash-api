package ankiflash.card.service.impl.dictionary;

import ankiflash.card.utility.Constants;
import ankiflash.card.utility.HtmlHelper;
import ankiflash.card.utility.Translation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WordReferenceDictionaryServiceImpl extends DictionaryServiceImpl {

  private static final Logger logger =
      LoggerFactory.getLogger(WordReferenceDictionaryServiceImpl.class);

  @Override
  public boolean isConnectionFailed(String word, Translation translation) {

    this.word = word;

    boolean isConnectionFailed = true;
    String url = HtmlHelper.lookupUrl(Constants.WORD_REFERENCE_URL_EN_SP, word);
    doc = HtmlHelper.getDocument(url);
    if (doc != null) {
      isConnectionFailed = false;
    }
    return isConnectionFailed;
  }

  @Override
  public boolean isWordNotFound() {

    String title = HtmlHelper.getText(doc, "title", 0);
    if (title.contains(Constants.WORD_REFERENCE_SPELLING_WRONG)) {
      return true;
    }

    String word = HtmlHelper.getText(doc, "span.headword>span", 0);
    return word.isEmpty();
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
    return "Word Reference Dictionary";
  }
}
