package ankiflash.card.service.impl.card;

import ankiflash.card.dto.Card;
import ankiflash.card.service.CardService;
import ankiflash.card.utility.Translation;
import ankiflash.utility.IOUtility;
import ankiflash.utility.AnkiFlashProps;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public abstract class CardServiceImpl implements CardService {

  public abstract Card generateCard(String word, Translation translation, String username);

  public List<Card> generateCards(List<String> words, Translation translation, String username) {
    List<Card> cardCollection = new ArrayList<>();
    for (String word : words) {
      cardCollection.add(generateCard(word, translation, username));
    }
    return cardCollection;
  }

  public String compressResources(String username) {

    ClassLoader classLoader = getClass().getClassLoader();
    String attachmentPath = classLoader.getResource("attachment").getPath();

    String ankiDir = Paths.get(username, AnkiFlashProps.ANKI_DIR_FLASHCARDS).toString();
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
    translations.add(Translation.VN_JP);

    // FRENCH
    translations.add(Translation.FR_EN);
    translations.add(Translation.FR_VN);

    // JAPANESE
    translations.add(Translation.JP_EN);
    translations.add(Translation.JP_VN);

    // CHINESE
    // SPANISH

    return translations;
  }
}