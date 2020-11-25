package ankiflash.card.service.impl.dictionary;

import ankiflash.card.service.DictionaryContentService;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public abstract class DictionaryContentServiceImpl implements DictionaryContentService {

  private static final Logger logger = LoggerFactory.getLogger(DictionaryContentServiceImpl.class);

  Document doc;

  String word;

  String wordId;

  String originalWord;

  String ankiDir;

  String imageOnline;

  String imageOffline;

  String imageLink;

  String soundOnline;

  String soundOffline;

  String soundLinks;

  String type;

  String phonetic;

  public String getImageOnline() {
    return imageOnline;
  }

  public String getImageOffline() {
    return imageOffline;
  }

  public String getImageLink() {
    return imageLink;
  }

  public String getSoundOnline() {
    return soundOnline;
  }

  public String getSoundOffline() {
    return soundOffline;
  }

  public String getSoundLinks() {
    return soundLinks;
  }

  public String getTag() {
    return word.substring(0, 1);
  }
}
