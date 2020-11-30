package ankiflash.card.service.impl.card;

import ankiflash.card.dto.Card;
import ankiflash.card.service.DictionaryContentService;
import ankiflash.card.service.impl.dictionary.CambridgeDictionaryContentServiceImpl;
import ankiflash.card.service.impl.dictionary.LacVietDictionaryContentServiceImpl;
import ankiflash.card.service.impl.dictionary.OxfordDictionaryContentServiceImpl;
import ankiflash.card.utility.*;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class EnglishCardGeneratingServiceImpl extends CardGeneratingServiceImpl {

  private static final Logger logger =
      LoggerFactory.getLogger(EnglishCardGeneratingServiceImpl.class);

  @Override
  public List<String> getWords(String word, Translation translation) {

    word = word.toLowerCase();
    List<String> foundWords = new ArrayList<>();
    if (translation.equals(Translation.EN_EN)) {
      foundWords.addAll(CardHelper.getOxfordWords(word));
    } else {
      foundWords.add(word + Constant.SUB_DELIMITER + word + Constant.SUB_DELIMITER + word);
    }

    return foundWords;
  }

  @Override
  public Card generateCard(
      String combinedWord, Translation translation, String ankiDir, boolean isOffline) {

    combinedWord = combinedWord.toLowerCase();
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

    DictionaryContentService oxfordDict = new OxfordDictionaryContentServiceImpl();
    DictionaryContentService cambridgeDict = new CambridgeDictionaryContentServiceImpl();
    DictionaryContentService lacVietDict = new LacVietDictionaryContentServiceImpl();

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

    // English to English
    if (translation.equals(Translation.EN_EN)) {
      if (oxfordDict.isConnected(combinedWord, translation)) {
        card.setStatus(Status.Connection_Failed);
        card.setComment(Constant.CONNECTION_FAILED);
        return card;
      } else if (oxfordDict.isInvalidWord()) {
        card.setStatus(Status.Word_Not_Found);
        card.setComment(Constant.WORD_NOT_FOUND);
        return card;
      }

      card.setWordType(oxfordDict.getWordType());
      card.setPhonetic(oxfordDict.getPhonetic());
      card.setMeaning(oxfordDict.getMeaning());
      card.setCopyright(String.format(Constant.COPYRIGHT, oxfordDict.getDictionaryName()));

      // English to Chinese/French/Japanese
    } else if (translation.equals(Translation.EN_CN_TD)
        || translation.equals(Translation.EN_CN_SP)
        || translation.equals(Translation.EN_JP)
        || translation.equals(Translation.EN_FR)) {

      if (oxfordDict.isConnected(combinedWord, translation)
          || cambridgeDict.isConnected(combinedWord, translation)) {
        card.setStatus(Status.Connection_Failed);
        card.setComment(Constant.CONNECTION_FAILED);
        return card;
      } else if (oxfordDict.isInvalidWord() || cambridgeDict.isInvalidWord()) {
        card.setStatus(Status.Word_Not_Found);
        card.setComment(Constant.WORD_NOT_FOUND);
        return card;
      }

      card.setWordType(oxfordDict.getWordType());
      card.setPhonetic(oxfordDict.getPhonetic());
      card.setMeaning(cambridgeDict.getMeaning());
      card.setCopyright(
          String.format(
              Constant.COPYRIGHT,
              String.join(
                  ", and ", oxfordDict.getDictionaryName(), cambridgeDict.getDictionaryName())));

      // English to Vietnamese
    } else if (translation.equals(Translation.EN_VN)) {

      if (oxfordDict.isConnected(combinedWord, translation)
          || lacVietDict.isConnected(combinedWord, translation)) {
        card.setStatus(Status.Connection_Failed);
        card.setComment(Constant.CONNECTION_FAILED);
        return card;
      } else if (oxfordDict.isInvalidWord() || lacVietDict.isInvalidWord()) {
        card.setStatus(Status.Word_Not_Found);
        card.setComment(Constant.WORD_NOT_FOUND);
        return card;
      }

      card.setWordType(oxfordDict.getWordType());
      card.setPhonetic(oxfordDict.getPhonetic());
      card.setMeaning(lacVietDict.getMeaning());
      card.setCopyright(
          String.format(
              Constant.COPYRIGHT,
              String.join(
                  ", and ", oxfordDict.getDictionaryName(), lacVietDict.getDictionaryName())));

    } else {
      card.setStatus(Status.Not_Supported_Translation);
      card.setComment(
          String.format(
              Constant.NOT_SUPPORTED_TRANSLATION,
              translation.getSource(),
              translation.getTarget()));
      return card;
    }

    oxfordDict.getSounds(ankiDir, isOffline);
    card.setSoundOnline(oxfordDict.getSoundOnline());
    card.setSoundOffline(oxfordDict.getSoundOffline());
    card.setSoundLink(oxfordDict.getSoundLinks());

    oxfordDict.getImages(ankiDir, isOffline);
    card.setImageOffline(oxfordDict.getImageOffline());
    card.setImageOnline(oxfordDict.getImageOnline());
    card.setImageLink(oxfordDict.getImageLink());

    card.setExample(oxfordDict.getExample());
    card.setTag(oxfordDict.getTag());
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