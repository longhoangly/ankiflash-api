package theflash.flashcard.service.impl.card;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import theflash.flashcard.dto.Card;
import theflash.flashcard.service.CardService;
import theflash.flashcard.utils.Constants;
import theflash.flashcard.utils.Translation;
import theflash.utility.IOUtility;
import theflash.utility.TheFlashProperties;
import theflash.utility.exception.BadRequestException;

public abstract class CardServiceImpl implements CardService {

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

  public List<Translation> getSupportedLanguages() {

    List<Translation> translations = new ArrayList<>();
    // CHINESE
    translations.add(new Translation(Constants.CHINESE, Constants.ENGLISH));
    translations.add(new Translation(Constants.CHINESE, Constants.VIETNAMESE));
    //ENGLISH
    translations.add(new Translation(Constants.ENGLISH, Constants.ENGLISH));
    translations.add(new Translation(Constants.ENGLISH, Constants.CHINESE));
    translations.add(new Translation(Constants.ENGLISH, Constants.VIETNAMESE));
    //FRENCH
    translations.add(new Translation(Constants.FRENCH, Constants.ENGLISH));
    translations.add(new Translation(Constants.FRENCH, Constants.VIETNAMESE));
    //JAPANESE
    translations.add(new Translation(Constants.JAPANESE, Constants.ENGLISH));
    translations.add(new Translation(Constants.JAPANESE, Constants.VIETNAMESE));
    //SPANISH
    translations.add(new Translation(Constants.SPANISH, Constants.ENGLISH));
    translations.add(new Translation(Constants.SPANISH, Constants.VIETNAMESE));
    //VIETNAMESE
    translations.add(new Translation(Constants.VIETNAMESE, Constants.ENGLISH));
    translations.add(new Translation(Constants.VIETNAMESE, Constants.FRENCH));

    return translations;
  }
}