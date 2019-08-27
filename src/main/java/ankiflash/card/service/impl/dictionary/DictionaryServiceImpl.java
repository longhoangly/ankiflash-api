package ankiflash.card.service.impl.dictionary;

import ankiflash.card.service.DictionaryService;
import ankiflash.card.utility.DictHelper;
import ankiflash.card.utility.Translation;
import ankiflash.utility.IOUtility;
import java.io.File;
import java.nio.file.Paths;
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

  public abstract boolean isConnectionFailed(String combinedWord, Translation translation);

  public abstract boolean isWordNotFound();

  public abstract String getWordType();

  public abstract String getExample();

  public abstract String getPhonetic();

  public abstract void preProceedImage(String ankiDir, String selector);

  public void downloadImage() {

    File dir = new File(ankiDir);
    if (dir.exists() && !imageLink.isEmpty()) {
      String output = Paths.get(dir.getAbsolutePath(), imageName).toString();
      IOUtility.download(imageLink, output);
    } else {
      logger.warn("AnkiFlash folder not found! => isOffline ~ false" + ankiDir);
    }
  }

  public void downloadImage(String ankiDir, String imageLink) {

    String imageName = DictHelper.getLastElement(imageLink);
    File dir = new File(ankiDir);
    if (dir.exists() && !imageLink.isEmpty()) {
      String output = Paths.get(dir.getAbsolutePath(), imageName).toString();
      IOUtility.download(imageLink, output);
    } else {
      logger.warn("AnkiFlash folder not found! => isOffline ~ false" + ankiDir);
    }
  }

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

  public abstract void preProceedSound(String ankiDir, String selector);

  public void downloadSound() {

    File dir = new File(ankiDir);
    System.out.println(soundLink);
    if (dir.exists() && !soundLink.isEmpty()) {
      String output = Paths.get(dir.getAbsolutePath(), soundName).toString();
      IOUtility.download(soundLink, output);
    } else {
      logger.warn("AnkiFlash folder not found! " + ankiDir);
    }
  }

  public void downloadSound(String ankiDir, String soundLink) {

    String soundName = DictHelper.getLastElement(soundLink);
    File dir = new File(ankiDir);
    if (dir.exists() && !soundLink.isEmpty()) {
      String output = Paths.get(dir.getAbsolutePath(), soundName).toString();
      IOUtility.download(soundLink, output);
    } else {
      logger.warn("AnkiFlash folder not found! " + ankiDir);
    }
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

  public abstract String getMeaning();

  public String getTag() {
    return word.substring(0, 1);
  }

  public abstract String getDictionaryName();
}
