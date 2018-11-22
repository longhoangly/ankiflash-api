package theflash.flashcard.service.impl.card;

import theflash.flashcard.service.impl.dictionary.CambridgeBaseDictionaryServiceImpl;
import theflash.flashcard.service.impl.dictionary.LacVietBaseDictionaryServiceImpl;
import theflash.flashcard.service.impl.dictionary.OxfordBaseDictionaryServiceImpl;
import theflash.flashcard.utils.Constants;
import theflash.flashcard.utils.Translation;

public class EnglishBaseCardServiceImpl extends BaseCardServiceImpl {

  private String proUk;

  private String thumb;

  public String getProUk() {
    return proUk;
  }

  public String getThumb() {
    return thumb;
  }

  private OxfordBaseDictionaryServiceImpl oxfordDict = new OxfordBaseDictionaryServiceImpl();

  private CambridgeBaseDictionaryServiceImpl cambridgeDict = new CambridgeBaseDictionaryServiceImpl();

  private LacVietBaseDictionaryServiceImpl lacVietDict = new LacVietBaseDictionaryServiceImpl();

  @Override
  public String generateFlashcard(String word, Translation translation) {
    this.word = word;
    if (translation.equals(Translation.EN_EN)) {
      if (!oxfordDict.isConnectionEstablished(word, translation)) {
        return Constants.DICT_CONNECTION_FAILED;
      } else if (!oxfordDict.isWordingCorrect()) {
        return Constants.DICT_WORD_NOT_FOUND;
      }
      meaning = oxfordDict.getMeaning();
      copyRight = String.format(Constants.DICT_COPYRIGHT, oxfordDict.getDictionaryName());
    } else if (translation.equals(Translation.EN_CN)) {
      if (!oxfordDict.isConnectionEstablished(word, translation) ||
          !cambridgeDict.isConnectionEstablished(word, translation)) {
        return Constants.DICT_CONNECTION_FAILED;
      } else if (!oxfordDict.isWordingCorrect() ||
          !cambridgeDict.isWordingCorrect()) {
        return Constants.DICT_WORD_NOT_FOUND;
      }
      meaning = cambridgeDict.getMeaning();
      copyRight = String.format(Constants.DICT_COPYRIGHT, cambridgeDict.getDictionaryName());
    } else {
      if (!oxfordDict.isConnectionEstablished(word, translation) ||
          !lacVietDict.isConnectionEstablished(word, translation)) {
        return Constants.DICT_CONNECTION_FAILED;
      } else if (!oxfordDict.isWordingCorrect() ||
          !lacVietDict.isWordingCorrect()) {
        return Constants.DICT_WORD_NOT_FOUND;
      }
      meaning = lacVietDict.getMeaning();
      copyRight = String.format(Constants.DICT_COPYRIGHT,
          String.join(", and", oxfordDict.getDictionaryName(), lacVietDict.getDictionaryName()));
    }
    wordType = oxfordDict.getWordType();
    phonetic = oxfordDict.getPhonetic();
    example = oxfordDict.getExample();
    pron = oxfordDict.getPron("div.pron-us");
    image = oxfordDict.getImage("a[class=topic]", "href");
    tag = oxfordDict.getTag();
    proUk = oxfordDict.getPron("div.pron-uk");
    thumb = oxfordDict.getImage("img.thumb", "src");
    return cardContent =
        word + Constants.TAB + wordType + Constants.TAB + phonetic + Constants.TAB + example
            + Constants.TAB +
            proUk + Constants.TAB + pron + Constants.TAB + thumb + Constants.TAB + image
            + Constants.TAB +
            meaning + Constants.TAB + copyRight + Constants.TAB + tag + "\n";
  }
}
