package ankiflash.card.utility;

public class Constants {

  // ANKI
  public static final String ANKI_DECK = "AnkiDeck.csv";
  public static final String ANKI_FAILURE = "Failures.csv";
  public static final String ANKI_FLASH_SINGLE_FORM_TEMPLATE = "AnkiFlashFormTemplate.apkg";

  // LANGUAGES
  public static final String ENGLISH = "English";
  public static final String FRENCH = "French";
  public static final String VIETNAMESE = "Vietnamese";
  public static final String CHINESE_TD = "Chinese (Traditional)";
  public static final String CHINESE_SP = "Chinese (Simplified)";
  public static final String JAPANESE = "Japanese";
  public static final String SPANISH = "Spanish";

  // OXFORD
  public static final String OXFORD_SPELLING_WRONG = "Did you spell it correctly?";
  public static final String OXFORD_WORD_NOT_FOUND =
      "Oxford Learner's Dictionaries | Find the meanings";
  public static final String OXFORD_URL_EN_EN =
      "https://www.oxfordlearnersdictionaries.com/definition/english/%1$s";
  public static final String OXFORD_SEARCH_URL_EN_EN =
      "https://www.oxfordlearnersdictionaries.com/search/english/direct/?q=%1$s";

  // LACVIET
  public static final String LACVIET_SPELLING_WRONG = "Dữ liệu đang được cập nhật";
  public static final String LACVIET_URL_VN_EN =
      "http://tratu.coviet.vn/tu-dien-lac-viet.aspx?learn=hoc-tieng-anh&t=V-A&k=%1$s";
  public static final String LACVIET_URL_VN_FR =
      "http://tratu.coviet.vn/tu-dien-lac-viet.aspx?learn=hoc-tieng-phap&t=V-F&k=%1$s";
  public static final String LACVIET_URL_EN_VN =
      "http://tratu.coviet.vn/tu-dien-lac-viet.aspx?learn=hoc-tieng-anh&t=A-V&k=%1$s";
  public static final String LACVIET_URL_FR_VN =
      "http://tratu.coviet.vn/tu-dien-lac-viet.aspx?learn=hoc-tieng-phap&t=F-V&k=%1$s";

  // CAMBRIDGE
  public static final String CAMBRIDGE_SPELLING_WRONG = "Did you spell it correctly?";
  public static final String CAMBRIDGE_URL_EN_CN_TD =
      "https://dictionary.cambridge.org/search/english-chinese-traditional/direct/?q=%1$s";
  public static final String CAMBRIDGE_URL_EN_CN_SP =
      "https://dictionary.cambridge.org/search/english-chinese-simplified/direct/?q=%1$s";
  public static final String CAMBRIDGE_URL_EN_FR =
      "https://dictionary.cambridge.org/search/english-french/direct/?q=%1$s";
  public static final String CAMBRIDGE_URL_EN_JP =
      "https://dictionary.cambridge.org/search/english-japanese/direct/?q=%1$s";

  // COLLINS
  public static final String COLLINS_SPELLING_WRONG = "Sorry, no results for";
  public static final String COLLINS_URL_FR_EN =
      "https://www.collinsdictionary.com/search/?dictCode=french-english&q=%1$s";

  // JDICT
  public static final String JDICT_URL_VN_JP_OR_JP_VN = "https://kantan.vn/postrequest.ashx";

  // JISHO
  public static final String JISHO_WORD_NOT_FOUND = "Sorry, couldn't find anything matching";
  public static final String JISHO_WORD_URL_JP_EN = "https://jisho.org/word/%1$s";
  public static final String JISHO_SEARCH_URL_JP_EN = "https://jisho.org/search/%1$s";

  // WORD REFERENCE
  public static final String WORD_REFERENCE_SPELLING_WRONG = "";
  public static final String WORD_REFERENCE_URL_EN_SP = "";
  public static final String WORD_REFERENCE_URL_SP_EN = "";

  // CONSTANTS
  public static final String TAB = "\t";
  public static final String MAIN_DELIMITER = "\\*\\*\\*";
  public static final String SUB_DELIMITER = "===";
  public static final String NO_EXAMPLE = "No example {{c1::...}}";
  public static final String SUCCESS = "Success";
  public static final String COPYRIGHT =
      "This card's content is collected from the following dictionaries: %1$s";
  public static final String WORD_NOT_FOUND =
      "The word was not found! Could you please check spelling or report to us!";
  public static final String CONNECTION_FAILED =
      "Cannot connect to dictionaries, please try again later!";
  public static final String NOT_SUPPORTED_TRANSLATION =
      "The translation from %1$s to %2$s is not supported!";
}
