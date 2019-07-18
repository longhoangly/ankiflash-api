package ankiflash.card.service.impl.dictionary;

import ankiflash.card.utility.Constants;
import ankiflash.card.utility.HtmlHelper;
import ankiflash.card.utility.Translation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WordReferenceDictionaryServiceImpl extends DictionaryServiceImpl {

  private static final Logger logger = LoggerFactory.getLogger(WordReferenceDictionaryServiceImpl.class);

  @Override
  public boolean isConnectionEstablished(String word, Translation translation) {

    this.word = word;

    boolean isConnectionEstablished = false;
    String url = HtmlHelper.lookupUrl(Constants.DICT_WORD_REFERENCE_URL_EN_SP, word);
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
    if (title.contains(Constants.DICT_WORD_REFERENCE_SPELLING_WRONG)) {
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
    return "Word Reference Dictionary";
  }
}
