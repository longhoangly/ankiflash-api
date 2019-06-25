package theflash.flashcard.service.impl.card;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import theflash.flashcard.dto.Card;
import theflash.flashcard.service.CardService;
import theflash.flashcard.utils.Translation;
import theflash.utility.IOUtility;
import theflash.utility.TheFlashProperties;

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

    // ENGLISH
    translations.add(Translation.EN_EN);
    translations.add(Translation.EN_VN);
    translations.add(Translation.EN_CN_TD);
    translations.add(Translation.EN_CN_SP);
    translations.add(Translation.EN_FR);
    translations.add(Translation.EN_JP);

    // VIETNAMESE
    translations.add(Translation.VN_EN);
    translations.add(Translation.VN_FR);
    // translations.add(Translation.VN_JP);

    // FRENCH
    translations.add(Translation.FR_EN);
    translations.add(Translation.FR_VN);

    // JAPANESE
    // translations.add(Translation.JP_EN);
    // translations.add(Translation.JP_VN);

    // CHINESE
    // SPANISH

    return translations;
  }
}