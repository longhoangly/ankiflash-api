package ankiflash.card.service.impl.card;

import ankiflash.card.dto.Card;
import ankiflash.card.service.DictionaryService;
import ankiflash.card.service.impl.dictionary.CollinsDictionaryServiceImpl;
import ankiflash.card.service.impl.dictionary.LacVietDictionaryServiceImpl;
import ankiflash.card.utility.Constants;
import ankiflash.card.utility.Status;
import ankiflash.card.utility.Translation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FrenchCardServiceImpl extends CardServiceImpl {

  private static final Logger logger = LoggerFactory.getLogger(FrenchCardServiceImpl.class);

  @Override
  public Card generateCard(String word, Translation translation, String ankiDir) {

    logger.info("Word = " + word);
    logger.info("Source = " + translation.getSource());
    logger.info("Target = " + translation.getTarget());

    Card card = new Card(word);
    DictionaryService lacVietDict = new LacVietDictionaryServiceImpl();
    DictionaryService collinsDict = new CollinsDictionaryServiceImpl();

    // French to Vietnamese
    if (translation.equals(Translation.FR_VN)) {

      if (lacVietDict.isConnectionFailed(word, translation)) {
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

      if (collinsDict.isConnectionFailed(word, translation)) {
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
