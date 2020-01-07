package ankiflash.card.service.impl.card;

import ankiflash.card.dto.Card;
import ankiflash.card.service.DictionaryService;
import ankiflash.card.service.impl.dictionary.JDictDictionaryServiceImpl;
import ankiflash.card.service.impl.dictionary.LacVietDictionaryServiceImpl;
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
public class VietnameseCardServiceImpl extends CardServiceImpl {

  private static final Logger logger = LoggerFactory.getLogger(VietnameseCardServiceImpl.class);

  @Override
  public List<String> getWords(String word, Translation translation) {

    List<String> foundWords = new ArrayList<>();
    if (translation.equals(Translation.VN_JP)) {
      foundWords.addAll(CardHelper.getJDictWords(word));
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

    DictionaryService lacVietDict = new LacVietDictionaryServiceImpl();
    DictionaryService jDict = new JDictDictionaryServiceImpl();

    String hashCombination = combinedWord + Constants.SUB_DELIMITER + translation.toString();
    logger.info("finding-hash-combination={}", hashCombination);
    Card dbCard = cardDbService.findByHash(card.getHash());
    if (dbCard != null) {
      logger.info("card-found-from-our-DB={}", card.getWord());
      if (isOffline) {
        jDict.downloadImage(ankiDir, dbCard.getImageLink());
        jDict.downloadSound(ankiDir, dbCard.getSoundLink());
      }
      return dbCard;
    }

    // Vietnamese to English/French
    if (translation.equals(Translation.VN_EN) || translation.equals(Translation.VN_FR)) {

      if (lacVietDict.isConnectionFailed(combinedWord, translation)) {
        card.setStatus(Status.Connection_Failed);
        card.setComment(Constants.CONNECTION_FAILED);
        return card;
      } else if (lacVietDict.isWordNotFound()) {
        card.setStatus(Status.Word_Not_Found);
        card.setComment(Constants.WORD_NOT_FOUND);
        return card;
      }

      card.setWordType(lacVietDict.getWordType());
      card.setPhonetic(lacVietDict.getPhonetic());
      card.setExample(lacVietDict.getExample());

      lacVietDict.preProceedSound(ankiDir, "embed");
      if (isOffline) {
        lacVietDict.downloadSound();
      }
      card.setSoundOnline(lacVietDict.getSoundOnline());
      card.setSoundOffline(lacVietDict.getSoundOffline());
      card.setSoundLink(lacVietDict.getSoundLink());
      card.setSoundName(lacVietDict.getSoundName());

      lacVietDict.preProceedImage(ankiDir, "");
      card.setImageOffline(lacVietDict.getImageOffline());
      card.setImageOnline(lacVietDict.getImageOnline());
      card.setImageLink(lacVietDict.getImageLink());
      card.setImageName(lacVietDict.getImageName());

      card.setTag(lacVietDict.getTag());
      card.setMeaning(lacVietDict.getMeaning());
      card.setCopyright(String.format(Constants.COPYRIGHT, lacVietDict.getDictionaryName()));

      // Vietnamese to Japanese
    } else if (translation.equals(Translation.VN_JP)) {

      if (jDict.isConnectionFailed(combinedWord, translation)) {
        card.setStatus(Status.Connection_Failed);
        card.setComment(Constants.CONNECTION_FAILED);
        return card;
      } else if (jDict.isWordNotFound()) {
        card.setStatus(Status.Word_Not_Found);
        card.setComment(Constants.WORD_NOT_FOUND);
        return card;
      }

      card.setWordType(jDict.getWordType());
      card.setPhonetic(jDict.getPhonetic());
      card.setExample(jDict.getExample());

      jDict.preProceedSound(ankiDir, "a.sound");
      if (isOffline) {
        jDict.downloadSound();
      }
      card.setSoundOnline(jDict.getSoundOnline());
      card.setSoundOffline(jDict.getSoundOffline());
      card.setSoundLink(jDict.getSoundLink());
      card.setSoundName(jDict.getSoundName());

      jDict.preProceedImage(ankiDir, "a.fancybox.img");
      if (isOffline) {
        jDict.downloadImage();
      }
      card.setImageOffline(jDict.getImageOffline());
      card.setImageOnline(jDict.getImageOnline());
      card.setImageLink(jDict.getImageLink());
      card.setImageName(jDict.getImageName());

      card.setTag(jDict.getTag());
      card.setMeaning(jDict.getMeaning());
      card.setCopyright(String.format(Constants.COPYRIGHT, jDict.getDictionaryName()));

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
      logger.info("card-created-successfully-adding-to-DB: {}", card.getWord());
      cardDbService.save(card);
    }

    return card;
  }
}
