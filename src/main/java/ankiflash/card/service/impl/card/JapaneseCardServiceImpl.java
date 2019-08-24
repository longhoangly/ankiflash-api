package ankiflash.card.service.impl.card;

import ankiflash.card.dto.Card;
import ankiflash.card.service.DictionaryService;
import ankiflash.card.service.impl.dictionary.JDictDictionaryServiceImpl;
import ankiflash.card.service.impl.dictionary.JishoDictionaryServiceImpl;
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
public class JapaneseCardServiceImpl extends CardServiceImpl {

  private static final Logger logger = LoggerFactory.getLogger(JapaneseCardServiceImpl.class);

  @Override
  public List<String> getWords(String word, Translation translation) {

    List<String> foundWords = new ArrayList<>();
    if (translation.equals(Translation.JP_EN)) {
      foundWords.addAll(CardHelper.getJishoWords(word));
    } else if (translation.equals(Translation.JP_VN)) {
      foundWords.addAll(CardHelper.getJDictWords(word));
    } else {
      foundWords.add(word + ":" + word + ":" + word);
    }

    return foundWords;
  }

  @Override
  public Card generateCard(String combinedWord, Translation translation, String ankiDir) {

    Card card;
    String[] wordParts = combinedWord.split(":");
    if (combinedWord.contains(":") && wordParts.length == 3) {
      card = new Card(wordParts[0], wordParts[1], wordParts[2]);
    } else {
      throw new BadRequestException("Incorrect word format: " + combinedWord);
    }

    logger.info("Word = " + card.getWord());
    logger.info("WordId = " + card.getWordId());
    logger.info("OriginalWord = " + card.getOriginalWord());

    logger.info("Source = " + translation.getSource());
    logger.info("Target = " + translation.getTarget());

    String combineWord = card.getWord() + ":" + card.getWordId() + ":" + card.getOriginalWord();
    Card dbCard = cardDbService.findByHash(combineWord);
    logger.info("finding-hash={}", card.getWord());
    if (dbCard != null) {
      logger.info("card-found-from-our-DB..." + card.getWord());
      return dbCard;
    }

    DictionaryService jDict = new JDictDictionaryServiceImpl();
    DictionaryService jishoDict = new JishoDictionaryServiceImpl();

    // Japanese to Vietnamese
    if (translation.equals(Translation.JP_VN)) {

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
      card.setPron(jDict.getPron(ankiDir, ""));
      card.setImage(jDict.getImage(ankiDir, ""));
      card.setTag(jDict.getTag());
      card.setMeaning(jDict.getMeaning());
      card.setCopyright(String.format(Constants.COPYRIGHT, jDict.getDictionaryName()));

      // Japanese to English
    } else if (translation.equals(Translation.JP_EN)) {

      if (jishoDict.isConnectionFailed(combinedWord, translation)) {
        card.setStatus(Status.Connection_Failed);
        card.setComment(Constants.CONNECTION_FAILED);
        return card;
      } else if (jishoDict.isWordNotFound()) {
        card.setStatus(Status.Word_Not_Found);
        card.setComment(Constants.WORD_NOT_FOUND);
        return card;
      }

      card.setWordType(jishoDict.getWordType());
      card.setPhonetic(jishoDict.getPhonetic());
      card.setExample(jishoDict.getExample());
      card.setPron(jishoDict.getPron(ankiDir, ""));
      card.setImage(jishoDict.getImage(ankiDir, ""));
      card.setTag(jishoDict.getTag());
      card.setMeaning(jishoDict.getMeaning());
      card.setCopyright(String.format(Constants.COPYRIGHT, jishoDict.getDictionaryName()));

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
