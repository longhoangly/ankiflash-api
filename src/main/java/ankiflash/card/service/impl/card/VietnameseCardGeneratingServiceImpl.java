package ankiflash.card.service.impl.card;

import ankiflash.card.dto.Card;
import ankiflash.card.service.DictionaryContentService;
import ankiflash.card.service.impl.dictionary.JDictDictionaryContentServiceImpl;
import ankiflash.card.service.impl.dictionary.LacVietDictionaryContentServiceImpl;
import ankiflash.card.utility.*;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class VietnameseCardGeneratingServiceImpl extends CardGeneratingServiceImpl {

  private static final Logger logger =
      LoggerFactory.getLogger(VietnameseCardGeneratingServiceImpl.class);

  @Override
  public List<String> getWords(String word, Translation translation) {

    List<String> foundWords = new ArrayList<>();
    if (translation.equals(Translation.VN_JP)) {
      foundWords.addAll(CardHelper.getJDictWords(word));
    } else {
      foundWords.add(word + Constant.SUB_DELIMITER + word + Constant.SUB_DELIMITER + word);
    }

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
    DictionaryContentService jDict = new JDictDictionaryContentServiceImpl();

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

    // Vietnamese to English/French
    if (translation.equals(Translation.VN_EN) || translation.equals(Translation.VN_FR)) {

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

      // Vietnamese to Japanese
    } else if (translation.equals(Translation.VN_JP)) {

      if (jDict.isConnected(combinedWord, translation)) {
        card.setStatus(Status.Connection_Failed);
        card.setComment(Constant.CONNECTION_FAILED);
        return card;
      } else if (jDict.isInvalidWord()) {
        card.setStatus(Status.Word_Not_Found);
        card.setComment(Constant.WORD_NOT_FOUND);
        return card;
      }

      card.setWordType(jDict.getWordType());
      card.setPhonetic(jDict.getPhonetic());
      card.setExample(jDict.getExample());

      jDict.getSounds(ankiDir, isOffline);
      card.setSoundOnline(jDict.getSoundOnline());
      card.setSoundOffline(jDict.getSoundOffline());
      card.setSoundLink(jDict.getSoundLinks());

      jDict.getImages(ankiDir, isOffline);
      card.setImageOffline(jDict.getImageOffline());
      card.setImageOnline(jDict.getImageOnline());
      card.setImageLink(jDict.getImageLink());

      card.setTag(jDict.getTag());
      card.setMeaning(jDict.getMeaning());
      card.setCopyright(String.format(Constant.COPYRIGHT, jDict.getDictionaryName()));

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
