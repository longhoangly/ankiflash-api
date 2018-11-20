package theflash.base.cards;

import theflash.base.dictionary.CambridgeDictionary;
import theflash.base.dictionary.LacVietDictionary;
import theflash.base.dictionary.OxfordDictionary;
import theflash.base.utility.Constants;
import theflash.base.utility.Translation;

public class EnglishCard extends BaseCard {

  private String proUk;

  private String thumb;

  public String getProUk() {
    return proUk;
  }

  public String getThumb() {
    return thumb;
  }

  private OxfordDictionary oxfordDict = new OxfordDictionary();

  private CambridgeDictionary cambridgeDict = new CambridgeDictionary();

  private LacVietDictionary lacVietDict = new LacVietDictionary();

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
