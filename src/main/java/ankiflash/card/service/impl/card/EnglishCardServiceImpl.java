package ankiflash.card.service.impl.card;

import ankiflash.card.dto.Card;
import ankiflash.card.service.DictionaryService;
import ankiflash.card.service.impl.dictionary.CambridgeDictionaryServiceImpl;
import ankiflash.card.service.impl.dictionary.LacVietDictionaryServiceImpl;
import ankiflash.card.service.impl.dictionary.OxfordDictionaryServiceImpl;
import ankiflash.card.utility.CardHelper;
import ankiflash.card.utility.Constants;
import ankiflash.card.utility.Status;
import ankiflash.card.utility.Translation;
import ankiflash.utility.exception.BadRequestException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class EnglishCardServiceImpl extends CardServiceImpl {

  private static final Logger logger = LoggerFactory.getLogger(EnglishCardServiceImpl.class);

  @Override
  public List<String> getWords(String word, Translation translation) {

    List<String> foundWords = new ArrayList<>();
    if (translation.equals(Translation.EN_EN)) {
      foundWords.addAll(CardHelper.getOxfordWords(word));
    } else {
      foundWords.add(word + Constants.SUB_DELIMITER + word + Constants.SUB_DELIMITER + word);
    }

    return foundWords;
  }

  @Override
  public Card generateCard(
      String combinedWord, Translation translation, String ankiDir, boolean isOffline) {

    Card card;
    String[] wordParts = combinedWord.split(Constants.SUB_DELIMITER);
    if (combinedWord.contains(Constants.SUB_DELIMITER) && wordParts.length == 3) {
      card = new Card(wordParts[0], wordParts[1], wordParts[2], translation.toString());
    } else {
      throw new BadRequestException("Incorrect word format: " + combinedWord);
    }

    logger.info("Word = " + card.getWord());
    logger.info("WordId = " + card.getWordId());
    logger.info("OriginalWord = " + card.getOriginalWord());

    logger.info("Source = " + translation.getSource());
    logger.info("Target = " + translation.getTarget());

    DictionaryService oxfordDict = new OxfordDictionaryServiceImpl();
    DictionaryService cambridgeDict = new CambridgeDictionaryServiceImpl();
    DictionaryService lacVietDict = new LacVietDictionaryServiceImpl();

    String hashCombination = combinedWord + Constants.SUB_DELIMITER + translation.toString();
    logger.info("finding-hash-combination={}", hashCombination);
    Card dbCard = cardDbService.findByHash(card.getHash());
    if (dbCard != null) {
      logger.info("card-found-from-our-DB={}", card.getWord());
      if (isOffline) {
        oxfordDict.downloadImage(ankiDir, dbCard.getImageLink());
        oxfordDict.downloadSound(ankiDir, dbCard.getSoundLink());
      }
      return dbCard;
    }

    // English to English
    if (translation.equals(Translation.EN_EN)) {
      if (oxfordDict.isConnectionFailed(combinedWord, translation)) {
        card.setStatus(Status.Connection_Failed);
        card.setComment(Constants.CONNECTION_FAILED);
        return card;
      } else if (oxfordDict.isWordNotFound()) {
        card.setStatus(Status.Word_Not_Found);
        card.setComment(Constants.WORD_NOT_FOUND);
        return card;
      }

      card.setWordType(oxfordDict.getWordType());
      card.setPhonetic(oxfordDict.getPhonetic());
      card.setMeaning(oxfordDict.getMeaning());
      card.setCopyright(String.format(Constants.COPYRIGHT, oxfordDict.getDictionaryName()));

      // English to Chinese/French/Japanese
    } else if (translation.equals(Translation.EN_CN_TD)
        || translation.equals(Translation.EN_CN_SP)
        || translation.equals(Translation.EN_JP)
        || translation.equals(Translation.EN_FR)) {

      if (oxfordDict.isConnectionFailed(combinedWord, translation)
          || cambridgeDict.isConnectionFailed(combinedWord, translation)) {
        card.setStatus(Status.Connection_Failed);
        card.setComment(Constants.CONNECTION_FAILED);
        return card;
      } else if (oxfordDict.isWordNotFound() || cambridgeDict.isWordNotFound()) {
        card.setStatus(Status.Word_Not_Found);
        card.setComment(Constants.WORD_NOT_FOUND);
        return card;
      }

      card.setWordType(oxfordDict.getWordType());
      card.setPhonetic(oxfordDict.getPhonetic());
      card.setMeaning(cambridgeDict.getMeaning());
      card.setCopyright(
          String.format(
              Constants.COPYRIGHT,
              String.join(
                  ", and ", oxfordDict.getDictionaryName(), cambridgeDict.getDictionaryName())));

      // English to Vietnamese
    } else if (translation.equals(Translation.EN_VN)) {

      if (oxfordDict.isConnectionFailed(combinedWord, translation)
          || lacVietDict.isConnectionFailed(combinedWord, translation)) {
        card.setStatus(Status.Connection_Failed);
        card.setComment(Constants.CONNECTION_FAILED);
        return card;
      } else if (oxfordDict.isWordNotFound() || lacVietDict.isWordNotFound()) {
        card.setStatus(Status.Word_Not_Found);
        card.setComment(Constants.WORD_NOT_FOUND);
        return card;
      }

      card.setWordType(oxfordDict.getWordType());
      card.setPhonetic(oxfordDict.getPhonetic());
      card.setMeaning(lacVietDict.getMeaning());
      card.setCopyright(
          String.format(
              Constants.COPYRIGHT,
              String.join(
                  ", and ", oxfordDict.getDictionaryName(), lacVietDict.getDictionaryName())));

    } else {
      card.setStatus(Status.Not_Supported_Translation);
      card.setComment(
          String.format(
              Constants.NOT_SUPPORTED_TRANSLATION,
              translation.getSource(),
              translation.getTarget()));
      return card;
    }

    oxfordDict.preProceedSound(ankiDir, "div.pron-uk");
    if (isOffline) {
      oxfordDict.downloadSound();
    }
    oxfordDict.preProceedSound(ankiDir, "div.pron-us");
    if (isOffline) {
      oxfordDict.downloadSound();
    }
    card.setSoundOnline(oxfordDict.getSoundOnline());
    card.setSoundOffline(oxfordDict.getSoundOffline());
    card.setSoundLink(oxfordDict.getSoundLink());
    card.setSoundName(oxfordDict.getSoundName());

    oxfordDict.preProceedImage(ankiDir, "a.topic");
    if (isOffline) {
      oxfordDict.downloadImage();
    }
    card.setImageOffline(oxfordDict.getImageOffline());
    card.setImageOnline(oxfordDict.getImageOnline());
    card.setImageLink(oxfordDict.getImageLink());
    card.setImageName(oxfordDict.getImageName());

    card.setExample(oxfordDict.getExample());
    card.setTag(oxfordDict.getTag());
    card.setStatus(Status.Success);
    card.setComment(Constants.SUCCESS);

    if (card.getStatus().compareTo(Status.Success) == 0
        && cardDbService.findByHash(card.getHash()) == null
        && !ankiDir.isEmpty()) {
      logger.info("card-created-successfully-adding-to-DB: {}", card.getWord());
      cardDbService.save(card);
    }

    return card;
  }
}
