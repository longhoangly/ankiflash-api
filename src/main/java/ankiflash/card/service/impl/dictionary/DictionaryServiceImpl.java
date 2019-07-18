package ankiflash.card.service.impl.dictionary;

import ankiflash.card.service.DictionaryService;
import ankiflash.card.utility.Translation;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

@Service
public abstract class DictionaryServiceImpl implements DictionaryService {

  protected Document doc;

  protected String word;

  protected String type;

  protected String phonetic;

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
