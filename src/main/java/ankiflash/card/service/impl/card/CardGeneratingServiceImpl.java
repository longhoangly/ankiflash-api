package ankiflash.card.service.impl.card;

import ankiflash.card.dto.Card;
import ankiflash.card.service.CardGeneratingService;
import ankiflash.card.service.CardStorageService;
import ankiflash.card.utility.Translation;
import ankiflash.utility.IOUtility;
import ankiflash.utility.exception.ErrorHandler;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public abstract class CardGeneratingServiceImpl implements CardGeneratingService {

  @Autowired protected CardStorageService cardStorageService;

  public List<Card> generateCards(
      List<String> combinedWords, Translation translation, String ankiDir, boolean isOffline) {
    List<Card> cardCollection = new ArrayList<>();
    for (String combinedWord : combinedWords) {
      try {
        cardCollection.add(generateCard(combinedWord, translation, ankiDir, isOffline));
      } catch (Exception e) {
        ErrorHandler.log(e);
      }
    }
    return cardCollection;
  }

  public void compressResources(String ankiDir) {

    ClassLoader classLoader = getClass().getClassLoader();
    List<String> files = Arrays.asList("AnkiFlashTemplate.apkg", "anki.ico", "anki.png");

    for (String file : files) {
      InputStream inputStream =
          classLoader.getResourceAsStream(String.format("attachment/%s", file));
      try {
        FileUtils.copyInputStreamToFile(
            Objects.requireNonNull(inputStream), new File(ankiDir + "/" + file));
      } catch (Exception e) {
        ErrorHandler.log(e);
      }
    }

    IOUtility.zipFolder(ankiDir, ankiDir + ".zip");
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
