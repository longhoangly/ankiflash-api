package ankiflash.card.service.impl.dictionary;

import ankiflash.card.utility.Constant;
import ankiflash.card.utility.HtmlHelper;
import ankiflash.card.utility.Translation;
import ankiflash.utility.exception.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WordReferenceDictionaryContentServiceImpl extends DictionaryContentServiceImpl {

  private static final Logger logger =
      LoggerFactory.getLogger(WordReferenceDictionaryContentServiceImpl.class);

  @Override
  public boolean isConnected(String combinedWord, Translation translation) {

    String[] wordParts = combinedWord.split(Constant.SUB_DELIMITER);
    if (combinedWord.contains(Constant.SUB_DELIMITER) && wordParts.length == 3) {
      this.word = wordParts[0];
      this.wordId = wordParts[1];
      this.originalWord = wordParts[2];
    } else {
      throw new BadRequestException("Incorrect word format: " + combinedWord);
    }

    String url = HtmlHelper.lookupUrl(Constant.WORD_REFERENCE_URL_EN_SP, this.wordId);
    doc = HtmlHelper.getDocument(url);
    return doc == null;
  }

  @Override
  public boolean isInvalidWord() {

    String title = HtmlHelper.getText(doc, "title", 0);
    if (title.contains(Constant.WORD_REFERENCE_SPELLING_WRONG)) {
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
  public void getImages(String ankiDir, boolean isOffline) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void getSounds(String ankiDir, boolean isOffline) {
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
