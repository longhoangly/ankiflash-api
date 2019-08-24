package ankiflash.card.service.impl.card;

import ankiflash.card.dto.Card;
import ankiflash.card.service.DictionaryService;
import ankiflash.card.service.impl.dictionary.CollinsDictionaryServiceImpl;
import ankiflash.card.service.impl.dictionary.LacVietDictionaryServiceImpl;
import ankiflash.card.utility.Constants;
import ankiflash.card.utility.Status;
import ankiflash.card.utility.Translation;
import ankiflash.utility.exception.BadRequestException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class FrenchCardServiceImpl extends CardServiceImpl {

  private static final Logger logger = LoggerFactory.getLogger(FrenchCardServiceImpl.class);

  @Override
  public List<String> getWords(String word, Translation translation) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Card generateCard(String combinedWord, Translation translation, String ankiDir) {

    Card card;
    String[] wordParts = combinedWord.split(":");
    if (combinedWord.contains(":") && wordParts.length == 3) {
      card = new Card(wordParts[0], wordParts[1], wordParts[2], translation.toString());
    } else {
      throw new BadRequestException("Incorrect word format: " + combinedWord);
    }

    logger.info("Word = " + card.getWord());
    logger.info("WordId = " + card.getWordId());
    logger.info("OriginalWord = " + card.getOriginalWord());

    logger.info("Source = " + translation.getSource());
    logger.info("Target = " + translation.getTarget());

    String combineWord =
        card.getWord()
            + ":"
            + card.getWordId()
            + ":"
            + card.getOriginalWord()
            + ":"
            + translation.toString();
    Card dbCard = cardDbService.findByHash(combineWord);
    logger.info("finding-hash={}", card.getWord());
    if (dbCard != null) {
      logger.info("card-found-from-our-DB..." + card.getWord());
      return dbCard;
    }

    DictionaryService lacVietDict = new LacVietDictionaryServiceImpl();
    DictionaryService collinsDict = new CollinsDictionaryServiceImpl();

    // French to Vietnamese
    if (translation.equals(Translation.FR_VN)) {

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
      card.setPron(lacVietDict.getPron(ankiDir, "embed"));
      card.setImage(lacVietDict.getImage(ankiDir, ""));
      card.setTag(lacVietDict.getTag());
      card.setMeaning(lacVietDict.getMeaning());
      card.setCopyright(String.format(Constants.COPYRIGHT, lacVietDict.getDictionaryName()));

      // French to English
    } else if (translation.equals(Translation.FR_EN)) {

      if (collinsDict.isConnectionFailed(combinedWord, translation)) {
        card.setStatus(Status.Connection_Failed);
        card.setComment(Constants.CONNECTION_FAILED);
        return card;
      } else if (collinsDict.isWordNotFound()) {
        card.setStatus(Status.Word_Not_Found);
        card.setComment(Constants.WORD_NOT_FOUND);
        return card;
      }

      card.setWordType(collinsDict.getWordType());
      card.setPhonetic(collinsDict.getPhonetic());
      card.setExample(collinsDict.getExample());
      card.setPron(
          collinsDict.getPron(ankiDir, "a.hwd_sound.sound.audio_play_button.icon-volume-up.ptr"));
      card.setImage(collinsDict.getImage(ankiDir, ""));
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

    String cardContent =
        card.getWord()
            + Constants.TAB
            + card.getWordType()
            + Constants.TAB
            + card.getPhonetic()
            + Constants.TAB
            + card.getExample()
            + Constants.TAB
            + card.getPron()
            + Constants.TAB
            + card.getImage()
            + Constants.TAB
            + card.getMeaning()
            + Constants.TAB
            + card.getCopyright()
            + Constants.TAB
            + card.getTag()
            + "\n";
    card.setContent(cardContent);

    return card;
  }
}
