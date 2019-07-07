package theflash.flashcard.service.impl.card;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import theflash.flashcard.dto.Card;
import theflash.flashcard.service.DictionaryService;
import theflash.flashcard.service.impl.dictionary.JDictDictionaryServiceImpl;
import theflash.flashcard.utility.Constants;
import theflash.flashcard.utility.Status;
import theflash.flashcard.utility.Translation;

public class JapaneseCardServiceImpl extends CardServiceImpl {

  private static final Logger logger = LoggerFactory.getLogger(JapaneseCardServiceImpl.class);

  @Override
  public Card generateCard(String word, Translation translation, String username) {



    Card card = new Card(word);
    DictionaryService jDict = new JDictDictionaryServiceImpl();

    logger.info("Word = " + word);
    logger.info("Source = " + translation.getSource());
    logger.info("Target = " + translation.getTarget());

    // Japanese to Vietnamese
    if (translation.equals(Translation.JP_VN)) {

      if (!jDict.isConnectionEstablished(word, translation)) {
        card.setStatus(Status.CONNECTION_FAILED);
        card.setComment(Constants.DICT_CONNECTION_FAILED);
        return card;
      } else if (!jDict.isWordingCorrect()) {
        card.setStatus(Status.WORD_NOT_FOUND);
        card.setComment(Constants.DICT_WORD_NOT_FOUND);
        return card;
      }

      card.setWordType(jDict.getWordType());
      card.setPhonetic(jDict.getPhonetic());
      card.setMeaning(jDict.getMeaning());
      card.setCopyright(String.format(Constants.DICT_COPYRIGHT, jDict.getDictionaryName()));

      card.setExample(jDict.getExample());
      card.setPron(jDict.getPron(username, ""));
      card.setImage(jDict.getImage(username, ""));

    } else {
      card.setStatus(Status.NOT_SUPPORTED_TRANSLATION);
      card.setComment(String.format(Constants.DICT_NOT_SUPPORTED_TRANSLATION,
          translation.getSource(), translation.getTarget()));
      return card;
    }

    card.setTag(jDict.getTag());
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
