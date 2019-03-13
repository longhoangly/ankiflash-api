package theflash.flashcard.service.impl.card;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import theflash.flashcard.dto.Card;
import theflash.flashcard.utils.Constants;
import theflash.flashcard.utils.Status;
import theflash.flashcard.utils.Translation;

public class EnglishCardServiceImpl extends CardServiceImpl {

  private static final Logger logger = LoggerFactory.getLogger(EnglishCardServiceImpl.class);

  @Override
  public List<String> getSupportedLanguages() {
    List<String> languages = new ArrayList<>();
    languages.add(Constants.ENGLISH);
    languages.add(Constants.CHINESE);
    languages.add(Constants.VIETNAMESE);
    return languages;
  }

  @Override
  public Card generateCard(String word, Translation translation, String username) {
    Card card = new Card(word);

    logger.debug("Word = " + word);
    logger.debug("Translation = " + translation);

    //English to English
    if (translation.equals(Translation.EN_EN)) {

      if (!oxfordDict.isConnectionEstablished(word, translation)) {
        card.setStatus(Status.CONNECTION_FAILED);
        card.setComment(Constants.DICT_CONNECTION_FAILED);
        return card;
      } else if (!oxfordDict.isWordingCorrect()) {
        card.setStatus(Status.WORD_NOT_FOUND);
        card.setComment(Constants.DICT_WORD_NOT_FOUND);
        return card;
      }

      card.setMeaning(oxfordDict.getMeaning());
      card.setCopyright(String.format(Constants.DICT_COPYRIGHT, oxfordDict.getDictionaryName()));

      //English to Chinese
    } else if (translation.equals(Translation.EN_CN)) {

      if (!oxfordDict.isConnectionEstablished(word, translation) ||
          !cambridgeDict.isConnectionEstablished(word, translation)) {
        card.setStatus(Status.CONNECTION_FAILED);
        card.setComment(Constants.DICT_CONNECTION_FAILED);
        return card;
      } else if (!oxfordDict.isWordingCorrect() || !cambridgeDict.isWordingCorrect()) {
        card.setStatus(Status.WORD_NOT_FOUND);
        card.setComment(Constants.DICT_WORD_NOT_FOUND);
        return card;
      }

      card.setMeaning(cambridgeDict.getMeaning());
      card.setCopyright(String.format(Constants.DICT_COPYRIGHT,
          String.join(", and ", oxfordDict.getDictionaryName(), cambridgeDict.getDictionaryName())));

      //English to Vietnamese
    } else if (translation.equals(Translation.EN_VN)) {

      if (!oxfordDict.isConnectionEstablished(word, translation) ||
          !lacVietDict.isConnectionEstablished(word, translation)) {
        card.setStatus(Status.CONNECTION_FAILED);
        card.setComment(Constants.DICT_CONNECTION_FAILED);
        return card;
      } else if (!oxfordDict.isWordingCorrect() || !lacVietDict.isWordingCorrect()) {
        card.setStatus(Status.WORD_NOT_FOUND);
        card.setComment(Constants.DICT_WORD_NOT_FOUND);
        return card;
      }

      card.setMeaning(lacVietDict.getMeaning());
      card.setCopyright(String.format(Constants.DICT_COPYRIGHT,
          String.join(", and ", oxfordDict.getDictionaryName(), lacVietDict.getDictionaryName())));

    } else {
      card.setStatus(Status.NOT_SUPPORTED_TRANSLATION);
      card.setComment(String.format(Constants.DICT_NOT_SUPPORTED_TRANSLATION,
          translation.getSource(), translation.getTarget()));
      return card;
    }

    card.setWordType(oxfordDict.getWordType());
    card.setPhonetic(oxfordDict.getPhonetic());
    card.setExample(oxfordDict.getExample());
    card.setPron("BrE " + oxfordDict.getPron(username, "div.pron-uk")
        + " NAmE " + oxfordDict.getPron(username, "div.pron-us"));
    card.setImage(oxfordDict.getImage(username, "a[class=topic]"));
    card.setTag(oxfordDict.getTag());
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
