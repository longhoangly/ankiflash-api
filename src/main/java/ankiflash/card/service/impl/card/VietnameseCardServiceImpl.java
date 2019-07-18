package ankiflash.card.service.impl.card;

import ankiflash.card.dto.Card;
import ankiflash.card.service.DictionaryService;
import ankiflash.card.service.impl.dictionary.JDictDictionaryServiceImpl;
import ankiflash.card.service.impl.dictionary.LacVietDictionaryServiceImpl;
import ankiflash.card.utility.Constants;
import ankiflash.card.utility.Status;
import ankiflash.card.utility.Translation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VietnameseCardServiceImpl extends CardServiceImpl {

  private static final Logger logger = LoggerFactory.getLogger(VietnameseCardServiceImpl.class);

  @Override
  public Card generateCard(String word, Translation translation, String username) {

    Card card;
    String[] wordParts = word.split(":");
    if (word.contains(":") && wordParts.length == 3) {
      card = new Card(wordParts[0]);
    } else {
      card = new Card(word);
    }

    logger.info("Word = " + card.getWord());
    logger.info("Source = " + translation.getSource());
    logger.info("Target = " + translation.getTarget());

    DictionaryService lacVietDict = new LacVietDictionaryServiceImpl();
    DictionaryService jDict = new JDictDictionaryServiceImpl();

    // Vietnamese to English/French
    if (translation.equals(Translation.VN_EN)
        || translation.equals(Translation.VN_FR)) {

      if (!lacVietDict.isConnectionEstablished(word, translation)) {
        card.setStatus(Status.Connection_Failed);
        card.setComment(Constants.DICT_CONNECTION_FAILED);
        return card;
      } else if (!lacVietDict.isWordingCorrect()) {
        card.setStatus(Status.Word_Not_Found);
        card.setComment(Constants.DICT_WORD_NOT_FOUND);
        return card;
      }

      card.setWordType(lacVietDict.getWordType());
      card.setPhonetic(lacVietDict.getPhonetic());
      card.setExample(lacVietDict.getExample());
      card.setPron(lacVietDict.getPron(username, "embed"));
      card.setImage(lacVietDict.getImage(username, ""));
      card.setTag(lacVietDict.getTag());
      card.setMeaning(lacVietDict.getMeaning());
      card.setCopyright(String.format(Constants.DICT_COPYRIGHT, lacVietDict.getDictionaryName()));

      // Vietnamese to Japanese
    } else if (translation.equals(Translation.VN_JP)) {

      if (!jDict.isConnectionEstablished(word, translation)) {
        card.setStatus(Status.Connection_Failed);
        card.setComment(Constants.DICT_CONNECTION_FAILED);
        return card;
      } else if (!jDict.isWordingCorrect()) {
        card.setStatus(Status.Word_Not_Found);
        card.setComment(Constants.DICT_WORD_NOT_FOUND);
        return card;
      }

      card.setWordType(jDict.getWordType());
      card.setPhonetic(jDict.getPhonetic());
      card.setExample(jDict.getExample());
      card.setPron(jDict.getPron(username, ""));
      card.setImage(jDict.getImage(username, ""));
      card.setTag(jDict.getTag());
      card.setMeaning(jDict.getMeaning());
      card.setCopyright(String.format(Constants.DICT_COPYRIGHT, jDict.getDictionaryName()));

    } else {
      card.setStatus(Status.Not_Supported_Translation);
      card.setComment(String.format(Constants.DICT_NOT_SUPPORTED_TRANSLATION,
          translation.getSource(), translation.getTarget()));
      return card;
    }

    card.setStatus(Status.Success);
    card.setComment(Constants.DICT_SUCCESS);

    String cardContent = card.getWord() + Constants.TAB + card.getWordType() + Constants.TAB
        + card.getPhonetic() + Constants.TAB + card.getExample() + Constants.TAB + card.getPron() + Constants.TAB
        + card.getImage() + Constants.TAB + card.getMeaning() + Constants.TAB + card.getCopyright()
        + Constants.TAB + card.getTag() + "\n";
    card.setContent(cardContent);

    return card;
  }
}
