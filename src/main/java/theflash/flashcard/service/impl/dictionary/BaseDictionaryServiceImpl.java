package theflash.flashcard.service.impl.dictionary;

import java.util.List;
import org.jsoup.nodes.Document;
import theflash.flashcard.service.dictionary.DictionaryService;
import theflash.flashcard.utils.Translation;

public abstract class BaseDictionaryServiceImpl implements DictionaryService {

  protected Document doc;

  protected String word;

  protected Translation translation;

  public abstract List<Translation> supportedTranslations();

  public abstract boolean isConnectionEstablished(String word, Translation translation);

  public abstract boolean isWordingCorrect();

  public abstract String getWordType();

  public abstract String getExample();

  public abstract String getPhonetic();

  public abstract String getImage(String selector, String attr);

  public abstract String getPron(String selector);

  public abstract String getMeaning();

  public char getTag() {
    return word.charAt(0);
  }

  public abstract String getDictionaryName();
}
