package ankiflash.card.service.impl.dictionary;

import ankiflash.card.service.DictionaryService;
import ankiflash.card.utility.Translation;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

@Service
public abstract class DictionaryServiceImpl implements DictionaryService {

  Document doc;

  String word;

  String type;

  String phonetic;

  public abstract boolean isConnectionFailed(String word, Translation translation);

  public abstract boolean isWordNotFound();

  public abstract String getWordType();

  public abstract String getExample();

  public abstract String getPhonetic();

  public abstract String getImage(String username, String sessionId, String selector);

  public abstract String getPron(String username, String sessionId, String selector);

  public abstract String getMeaning();

  public char getTag() {
    return word.charAt(0);
  }

  public abstract String getDictionaryName();
}
