package ankiflash.card.service.impl.card;

import ankiflash.card.dto.Card;
import ankiflash.card.service.DictionaryContentService;
import ankiflash.card.service.impl.dictionary.CollinsDictionaryContentServiceImpl;
import ankiflash.card.service.impl.dictionary.LacVietDictionaryContentServiceImpl;
import ankiflash.card.utility.Constant;
import ankiflash.card.utility.DictHelper;
import ankiflash.card.utility.Status;
import ankiflash.card.utility.Translation;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class FrenchCardGeneratingServiceImpl extends CardGeneratingServiceImpl {

  private static final Logger logger =
      LoggerFactory.getLogger(FrenchCardGeneratingServiceImpl.class);

  @Override
  public List<String> getWords(String word, Translation translation) {

    List<String> foundWords = new ArrayList<>();
    foundWords.add(word + Constant.SUB_DELIMITER + word + Constant.SUB_DELIMITER + word);
    return foundWords;
  }

  @Override
  public Card generateCard(
      String combinedWord, Translation translation, String ankiDir, boolean isOffline) {

    Card card = new Card();
    String[] wordParts = combinedWord.split(Constant.SUB_DELIMITER);
    if (combinedWord.contains(Constant.SUB_DELIMITER) && wordParts.length == 3) {
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

    DictionaryContentService lacVietDict = new LacVietDictionaryContentServiceImpl();
    DictionaryContentService collinsDict = new CollinsDictionaryContentServiceImpl();

    String hashCombination = combinedWord + Constant.SUB_DELIMITER + translation.toString();
    logger.info("Finding-hash-combination={}", hashCombination);
    Card dbCard = cardStorageService.findByHash(card.getHash());
    if (dbCard != null) {
      logger.info("Card-found-from-our-DB={}", card.getWord());
      if (isOffline) {
        DictHelper.downloadFiles(ankiDir, dbCard.getImageLink());
        DictHelper.downloadFiles(ankiDir, dbCard.getSoundLink());
      }
      return dbCard;
    }

    // French to Vietnamese
    if (translation.equals(Translation.FR_VN)) {

      if (lacVietDict.isConnected(combinedWord, translation)) {
        card.setStatus(Status.Connection_Failed);
        card.setComment(Constant.CONNECTION_FAILED);
        return card;
      } else if (lacVietDict.isInvalidWord()) {
        card.setStatus(Status.Word_Not_Found);
        card.setComment(Constant.WORD_NOT_FOUND);
        return card;
      }

      card.setWordType(lacVietDict.getWordType());
      card.setPhonetic(lacVietDict.getPhonetic());
      card.setExample(lacVietDict.getExample());

      lacVietDict.getSounds(ankiDir, isOffline);
      card.setSoundOnline(lacVietDict.getSoundOnline());
      card.setSoundOffline(lacVietDict.getSoundOffline());
      card.setSoundLink(lacVietDict.getSoundLinks());

      lacVietDict.getImages(ankiDir, isOffline);
      card.setImageOffline(lacVietDict.getImageOffline());
      card.setImageOnline(lacVietDict.getImageOnline());
      card.setImageLink(lacVietDict.getImageLink());

      card.setTag(lacVietDict.getTag());
      card.setMeaning(lacVietDict.getMeaning());
      card.setCopyright(String.format(Constant.COPYRIGHT, lacVietDict.getDictionaryName()));

      // French to English
    } else if (translation.equals(Translation.FR_EN)) {

      if (collinsDict.isConnected(combinedWord, translation)) {
        card.setStatus(Status.Connection_Failed);
        card.setComment(Constant.CONNECTION_FAILED);
        return card;
      } else if (collinsDict.isInvalidWord()) {
        card.setStatus(Status.Word_Not_Found);
        card.setComment(Constant.WORD_NOT_FOUND);
        return card;
      }

      card.setWordType(collinsDict.getWordType());
      card.setPhonetic(collinsDict.getPhonetic());
      card.setExample(collinsDict.getExample());

      collinsDict.getSounds(ankiDir, isOffline);
      card.setSoundOnline(collinsDict.getSoundOnline());
      card.setSoundOffline(collinsDict.getSoundOffline());
      card.setSoundLink(collinsDict.getSoundLinks());

      collinsDict.getImages(ankiDir, isOffline);
      card.setImageOffline(collinsDict.getImageOffline());
      card.setImageOnline(collinsDict.getImageOnline());
      card.setImageLink(collinsDict.getImageLink());

      card.setTag(collinsDict.getTag());
      card.setMeaning(collinsDict.getMeaning());
      card.setCopyright(String.format(Constant.COPYRIGHT, lacVietDict.getDictionaryName()));

    } else {

      card.setStatus(Status.Not_Supported_Translation);
      card.setComment(
          String.format(
              Constant.NOT_SUPPORTED_TRANSLATION,
              translation.getSource(),
              translation.getTarget()));

      return card;
    }

    card.setStatus(Status.Success);
    card.setComment(Constant.SUCCESS);

    if (card.getStatus().compareTo(Status.Success) == 0
        && cardStorageService.findByHash(card.getHash()) == null
        && !ankiDir.isEmpty()) {
      logger.info("Card-created-successfully-adding-to-DB: {}", card.getWord());
      cardStorageService.save(card);
    }

    return card;
  }
}
