package theflash.flashcard.service.impl.card;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import theflash.flashcard.dto.Card;
import theflash.flashcard.service.DictionaryService;
import theflash.flashcard.service.impl.dictionary.CollinsDictionaryServiceImpl;
import theflash.flashcard.service.impl.dictionary.LacVietDictionaryServiceImpl;
import theflash.flashcard.utils.Constants;
import theflash.flashcard.utils.Status;
import theflash.flashcard.utils.Translation;

public class FrenchCardServiceImpl extends CardServiceImpl {

  private static final Logger logger = LoggerFactory.getLogger(FrenchCardServiceImpl.class);

  @Override
  public Card generateCard(String word, Translation translation, String username) {

    Card card = new Card(word);
    DictionaryService lacVietDict = new LacVietDictionaryServiceImpl();
    DictionaryService collinsDict = new CollinsDictionaryServiceImpl();

    logger.info("Word = " + word);
    logger.info("Target = " + translation.getTarget());

    // French to Vietnamese
    if (translation.equals(Translation.FR_VN)) {

      if (!lacVietDict.isConnectionEstablished(word, translation)) {
        card.setStatus(Status.CONNECTION_FAILED);
        card.setComment(Constants.DICT_CONNECTION_FAILED);
        return card;
      } else if (!lacVietDict.isWordingCorrect()) {
        card.setStatus(Status.WORD_NOT_FOUND);
        card.setComment(Constants.DICT_WORD_NOT_FOUND);
        return card;
      }

      card.setWordType(lacVietDict.getWordType());
      card.setPhonetic(lacVietDict.getPhonetic());
      card.setExample(lacVietDict.getExample());
      card.setPron(lacVietDict.getPron(username, "embed"));
      card.setImage(lacVietDict.getImage("any", "any"));
      card.setTag(lacVietDict.getTag());
      card.setMeaning(lacVietDict.getMeaning());
      card.setCopyright(String.format(Constants.DICT_COPYRIGHT, lacVietDict.getDictionaryName()));

      // French to English
    } else if (translation.equals(Translation.FR_EN)) {

      if (!collinsDict.isConnectionEstablished(word, translation)) {
        card.setStatus(Status.CONNECTION_FAILED);
        card.setComment(Constants.DICT_CONNECTION_FAILED);
        return card;
      } else if (!collinsDict.isWordingCorrect()) {
        card.setStatus(Status.WORD_NOT_FOUND);
        card.setComment(Constants.DICT_WORD_NOT_FOUND);
        return card;
      }

      card.setWordType(collinsDict.getWordType());
      card.setPhonetic(collinsDict.getPhonetic());
      card.setExample(collinsDict.getExample());
      card.setPron(collinsDict.getPron(username, "a.hwd_sound.sound.audio_play_button.icon-volume-up.ptr"));
      card.setImage(collinsDict.getImage("any", "any"));
      card.setTag(collinsDict.getTag());
      card.setMeaning(collinsDict.getMeaning());
      card.setCopyright(String.format(Constants.DICT_COPYRIGHT, lacVietDict.getDictionaryName()));

    } else {

      card.setStatus(Status.NOT_SUPPORTED_TRANSLATION);
      card.setComment(String.format(Constants.DICT_NOT_SUPPORTED_TRANSLATION,
          translation.getSource(), translation.getTarget()));

      return card;
    }

    card.setStatus(Status.SUCCESS);
    card.setComment(Constants.DICT_SUCCESS);

    String cardContent = card.getWord() + Constants.TAB + card.getWordType() + Constants.TAB
        + card.getPhonetic() + Constants.TAB + card.getExample() + Constants.TAB + card.getPron() + Constants.TAB
        + card.getImage() + Constants.TAB + card.getMeaning() + Constants.TAB + card.getCopyright()
        + Constants.TAB + card.getTag() + "\n";
    card.setContent(cardContent);

    return card;
  }

  @Override
  public List<Card> generateCards(List<String> words, Translation translation, String username) {
    List<Card> cardCollection = new ArrayList<>();
    for (String word : words) {
      cardCollection.add(generateCard(word, translation, username));
    }
    return cardCollection;
  }
}
