package ankiflash.card.service.impl.dictionary;

import ankiflash.card.service.DictionaryService;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public abstract class DictionaryServiceImpl implements DictionaryService {

  private static final Logger logger = LoggerFactory.getLogger(DictionaryServiceImpl.class);

  Document doc;

  String word;

  String wordId;

  String originalWord;

  String ankiDir;

  String imageOnline;

  String imageOffline;

  String imageName;

  String imageLink;

  String soundOnline;

  String soundOffline;

  String soundName;

  String soundLink;

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

  public String getImageName() {
    return imageName;
  }

  public String getSoundOnline() {
    return soundOnline;
  }

  public String getSoundOffline() {
    return soundOffline;
  }

  public String getSoundLink() {
    return soundLink;
  }

  public String getSoundName() {
    return soundName;
  }

  public String getTag() {
    return word.substring(0, 1);
  }
}
