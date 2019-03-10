package theflash.flashcard.service.impl.card;

import java.nio.file.Paths;
import java.util.List;
import theflash.flashcard.dto.Card;
import theflash.flashcard.service.CardService;
import theflash.flashcard.service.DictionaryService;
import theflash.flashcard.service.impl.dictionary.CambridgeDictionaryServiceImpl;
import theflash.flashcard.service.impl.dictionary.LacVietDictionaryServiceImpl;
import theflash.flashcard.service.impl.dictionary.OxfordDictionaryServiceImpl;
import theflash.flashcard.utils.Constants;
import theflash.flashcard.utils.Translation;
import theflash.utility.IOUtility;
import theflash.utility.TheFlashProperties;

public abstract class CardServiceImpl implements CardService {

  protected DictionaryService oxfordDict = new OxfordDictionaryServiceImpl();

  protected DictionaryService cambridgeDict = new CambridgeDictionaryServiceImpl();

  protected DictionaryService lacVietDict = new LacVietDictionaryServiceImpl();


  public abstract List<Translation> supportedTranslations();

  public abstract Card generateCard(String word, Translation translation, String username);

  public abstract List<Card> generateCards(List<String> wordList, Translation translation, String username);

  public String compressResources(String username) {

    ClassLoader classLoader = getClass().getClassLoader();
    String attachmentPath = classLoader.getResource("attachment").getPath();

    String ankiDir = Paths.get(username, TheFlashProperties.ANKI_DIR_FLASHCARDS).toString();
    IOUtility.copyFolder(attachmentPath, ankiDir);

    IOUtility.zipFolder(ankiDir, ankiDir + ".zip");
    return ankiDir + ".zip";
  }

  public static CardService getCardService(Translation translation) {

    CardService cardService = null;

    if (translation.getSource().equals(Constants.ENGLISH)) {

      cardService = new EnglishCardServiceImpl();

    } else if (translation.getSource().equals(Constants.VIETNAMESE)) {

      cardService = new VietnameseCardServiceImpl();

    } else if (translation.getSource().equals(Constants.CHINESE)) {

      cardService = new ChineseCardServiceImpl();

    } else if (translation.getSource().equals(Constants.JAPANESE)) {

      cardService = new JapaneseCardServiceImpl();

    } else if (translation.getSource().equals(Constants.SPANISH)) {

      cardService = new SpanishCardServiceImpl();

    }

    return cardService;
  }
}