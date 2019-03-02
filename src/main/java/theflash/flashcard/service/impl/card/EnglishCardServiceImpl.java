package theflash.flashcard.service.impl.card;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import theflash.flashcard.controller.CardController;
import theflash.flashcard.dto.Card;
import theflash.flashcard.service.impl.dictionary.CambridgeDictionaryServiceImpl;
import theflash.flashcard.service.impl.dictionary.LacVietDictionaryServiceImpl;
import theflash.flashcard.service.impl.dictionary.OxfordDictionaryServiceImpl;
import theflash.flashcard.utils.Constants;
import theflash.flashcard.utils.Status;
import theflash.flashcard.utils.Translation;
import theflash.utility.TheFlashProperties;

public class EnglishCardServiceImpl extends CardServiceImpl {

  private static final Logger logger = LoggerFactory.getLogger(CardController.class);

  private OxfordDictionaryServiceImpl oxfordDict = new OxfordDictionaryServiceImpl();

  private CambridgeDictionaryServiceImpl cambridgeDict = new CambridgeDictionaryServiceImpl();

  private LacVietDictionaryServiceImpl lacVietDict = new LacVietDictionaryServiceImpl();

  @Override
  public Card generateCard(String word, Translation translation) {
    Card card = new Card(word);

    //English to English
    if (translation.equals(Translation.EN_EN)) {

      if (!oxfordDict.isConnectionEstablished(word, translation)) {
        card.setStatus(Status.CONNECTION_FAILED);
        card.setComment(Constants.DICT_CONNECTION_FAILED);
      } else if (!oxfordDict.isWordingCorrect()) {
        card.setStatus(Status.WORD_NOT_FOUND);
        card.setComment(Constants.DICT_WORD_NOT_FOUND);
      }
      card.setMeaning(oxfordDict.getMeaning());
      card.setCopyright(String.format(Constants.DICT_COPYRIGHT, oxfordDict.getDictionaryName()));

      //English to Chinese
    } else if (translation.equals(Translation.EN_CN)) {

      if (!oxfordDict.isConnectionEstablished(word, translation) ||
          !cambridgeDict.isConnectionEstablished(word, translation)) {
        card.setStatus(Status.CONNECTION_FAILED);
        card.setComment(Constants.DICT_CONNECTION_FAILED);
      } else if (!oxfordDict.isWordingCorrect() ||
          !cambridgeDict.isWordingCorrect()) {
        card.setStatus(Status.WORD_NOT_FOUND);
        card.setComment(Constants.DICT_WORD_NOT_FOUND);
      }
      card.setMeaning(cambridgeDict.getMeaning());
      card.setCopyright(String.format(Constants.DICT_COPYRIGHT,
          String.join(", and ", oxfordDict.getDictionaryName(), cambridgeDict.getDictionaryName())));

      //English to Vietnamese
    } else {
      if (!oxfordDict.isConnectionEstablished(word, translation) ||
          !lacVietDict.isConnectionEstablished(word, translation)) {
        card.setStatus(Status.CONNECTION_FAILED);
        card.setComment(Constants.DICT_CONNECTION_FAILED);
      } else if (!oxfordDict.isWordingCorrect() ||
          !lacVietDict.isWordingCorrect()) {
        card.setStatus(Status.WORD_NOT_FOUND);
        card.setComment(Constants.DICT_WORD_NOT_FOUND);
      }
      card.setMeaning(lacVietDict.getMeaning());
      card.setCopyright(String.format(Constants.DICT_COPYRIGHT,
          String.join(", and ", oxfordDict.getDictionaryName(), lacVietDict.getDictionaryName())));
    }

    card.setWordType(oxfordDict.getWordType());
    card.setPhonetic(oxfordDict.getPhonetic());
    card.setExample(oxfordDict.getExample());
    card.setPron("BrE " + oxfordDict.getPron("div.pron-uk") + " NAmE " + oxfordDict.getPron("div.pron-us"));
    card.setImage(oxfordDict.getImage("a[class=topic]", "href"));
    card.setTag(oxfordDict.getTag());
    card.setStatus(Status.SUCCESS);
    card.setComment("Success");

    String cardContent =
        card.getWord() + Constants.TAB + card.getWordType() + Constants.TAB + card.getPhonetic() + Constants.TAB
            + card.getExample() + Constants.TAB + card.getPron() + Constants.TAB + card.getImage() + Constants.TAB
            + card.getMeaning() + Constants.TAB + card.getCopyright() + Constants.TAB + card.getTag() + "\n";
    card.setContent(cardContent);

    try {
      FileUtils.write(new File(Paths.get(TheFlashProperties.ANKI_DIR_FLASHCARDS, "flashcards.csv").toString()),
          cardContent, Charset.defaultCharset());
    } catch (IOException e) {
      logger.error("Exception: ", e);
    }
    return card;
  }

  @Override
  public List<Card> generateCards(List<String> wordList, Translation translation) {
    List<Card> cardCollection = new ArrayList<>();
    for (String word : wordList) {
      cardCollection.add(generateCard(word, translation));
    }
    return cardCollection;
  }

  @Override
  public String getDownloadLink() {
    return null;
  }
}
