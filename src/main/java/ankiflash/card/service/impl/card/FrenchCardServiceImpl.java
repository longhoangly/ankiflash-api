package ankiflash.card.service.impl.card;

import ankiflash.card.dto.Card;
import ankiflash.card.service.DictionaryService;
import ankiflash.card.service.impl.dictionary.CollinsDictionaryServiceImpl;
import ankiflash.card.service.impl.dictionary.LacVietDictionaryServiceImpl;
import ankiflash.card.utility.Constants;
import ankiflash.card.utility.Status;
import ankiflash.card.utility.Translation;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class FrenchCardServiceImpl extends CardServiceImpl {

  private static final Logger logger = LoggerFactory.getLogger(FrenchCardServiceImpl.class);

  @Override
  public List<String> getWords(String word, Translation translation) {

    List<String> foundWords = new ArrayList<>();
    foundWords.add(word + Constants.SUB_DELIMITER + word + Constants.SUB_DELIMITER + word);
    return foundWords;
  }

  @Override
  public Card generateCard(
      String combinedWord, Translation translation, String ankiDir, boolean isOffline) {

    Card card = new Card();
    String[] wordParts = combinedWord.split(Constants.SUB_DELIMITER);
    if (combinedWord.contains(Constants.SUB_DELIMITER) && wordParts.length == 3) {
      card = new Card(wordParts[0], wordParts[1], wordParts[2], translation.toString());
    } else {
      card.setStatus(Status.Word_Not_Found);
      card.setComment("Incorrect word format=" + combinedWord);
      return card;
    }

    logger.info("Word = {}", card.getWord());
    logger.info("WordId = {}", card.getWordId());
    logger.info("OriginalWord = {}", card.getOriginalWord());

    logger.info("Source = {}", translation.getSource());
    logger.info("Target = {}", translation.getTarget());

    DictionaryService lacVietDict = new LacVietDictionaryServiceImpl();
    DictionaryService collinsDict = new CollinsDictionaryServiceImpl();

    String hashCombination = combinedWord + Constants.SUB_DELIMITER + translation.toString();
    logger.info("Finding-hash-combination={}", hashCombination);
    Card dbCard = cardDbService.findByHash(card.getHash());
    if (dbCard != null) {
      logger.info("Card-found-from-our-DB={}", card.getWord());
      if (isOffline) {
        collinsDict.downloadFile(ankiDir, dbCard.getImageLink());
        collinsDict.downloadFile(ankiDir, dbCard.getSoundLink());
      }
      return dbCard;
    }

    // French to Vietnamese
    if (translation.equals(Translation.FR_VN)) {

      if (lacVietDict.isConnected(combinedWord, translation)) {
        card.setStatus(Status.Connection_Failed);
        card.setComment(Constants.CONNECTION_FAILED);
        return card;
      } else if (lacVietDict.isInvalidWord()) {
        card.setStatus(Status.Word_Not_Found);
        card.setComment(Constants.WORD_NOT_FOUND);
        return card;
      }

      card.setWordType(lacVietDict.getWordType());
      card.setPhonetic(lacVietDict.getPhonetic());
      card.setExample(lacVietDict.getExample());

      lacVietDict.getSounds(ankiDir, "embed");
      if (isOffline) {
        lacVietDict.downloadSound();
      }
      card.setSoundOnline(lacVietDict.getSoundOnline());
      card.setSoundOffline(lacVietDict.getSoundOffline());
      card.setSoundLink(lacVietDict.getSoundLink());

      lacVietDict.getImages(ankiDir, "");
      card.setImageOffline(lacVietDict.getImageOffline());
      card.setImageOnline(lacVietDict.getImageOnline());
      card.setImageLink(lacVietDict.getImageLink());

      card.setTag(lacVietDict.getTag());
      card.setMeaning(lacVietDict.getMeaning());
      card.setCopyright(String.format(Constants.COPYRIGHT, lacVietDict.getDictionaryName()));

      // French to English
    } else if (translation.equals(Translation.FR_EN)) {

      if (collinsDict.isConnected(combinedWord, translation)) {
        card.setStatus(Status.Connection_Failed);
        card.setComment(Constants.CONNECTION_FAILED);
        return card;
      } else if (collinsDict.isInvalidWord()) {
        card.setStatus(Status.Word_Not_Found);
        card.setComment(Constants.WORD_NOT_FOUND);
        return card;
      }

      card.setWordType(collinsDict.getWordType());
      card.setPhonetic(collinsDict.getPhonetic());
      card.setExample(collinsDict.getExample());

      collinsDict.getSounds(
          ankiDir, "a.hwd_sound.sound.audio_play_button.icon-volume-up.ptr");
      if (isOffline) {
        collinsDict.downloadSound();
      }
      card.setSoundOnline(collinsDict.getSoundOnline());
      card.setSoundOffline(collinsDict.getSoundOffline());
      card.setSoundLink(collinsDict.getSoundLink());

      collinsDict.getImages(ankiDir, "");
      card.setImageOffline(collinsDict.getImageOffline());
      card.setImageOnline(collinsDict.getImageOnline());
      card.setImageLink(collinsDict.getImageLink());

      card.setTag(collinsDict.getTag());
      card.setMeaning(collinsDict.getMeaning());
      card.setCopyright(String.format(Constants.COPYRIGHT, lacVietDict.getDictionaryName()));

    } else {

      card.setStatus(Status.Not_Supported_Translation);
      card.setComment(
          String.format(
              Constants.NOT_SUPPORTED_TRANSLATION,
              translation.getSource(),
              translation.getTarget()));

      return card;
    }

    card.setStatus(Status.Success);
    card.setComment(Constants.SUCCESS);

    if (card.getStatus().compareTo(Status.Success) == 0
        && cardDbService.findByHash(card.getHash()) == null
        && !ankiDir.isEmpty()) {
      logger.info("Card-created-successfully-adding-to-DB: {}", card.getWord());
      cardDbService.save(card);
    }

    return card;
  }
}
