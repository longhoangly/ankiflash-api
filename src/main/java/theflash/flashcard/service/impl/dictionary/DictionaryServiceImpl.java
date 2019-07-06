package theflash.flashcard.service.impl.dictionary;

import org.jsoup.nodes.Document;
import theflash.flashcard.service.DictionaryService;
import theflash.flashcard.utility.Translation;

public abstract class DictionaryServiceImpl implements DictionaryService {

  protected Document doc;

  protected String word;

  protected String type;

  protected String phonetic;

  protected Translation translation;

  public abstract boolean isConnectionEstablished(String word, Translation translation);

  public abstract boolean isWordingCorrect();

  public abstract String getWordType();

  public abstract String getExample();

  public abstract String getPhonetic();

  public abstract String getImage(String username, String selector);

  public abstract String getPron(String username, String selector);

  public abstract String getMeaning();

  public char getTag() {
    return word.charAt(0);
  }

  public abstract String getDictionaryName();
}
