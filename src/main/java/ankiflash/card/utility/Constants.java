package ankiflash.card.utility;

public class Constants {

  // ANKI FILES
  public static String ANKI_DECK = "ankiDeck.csv";
  public static String ANKI_FAILURE = "failures.csv";
  public static String ANKI_LANGUAGE = "Language.csv";

  // ANKI MODELS
  public static String ANKI_GENERATOR_SINGLE_FORM_ABC = "AnkiGeneratorSingleFormABC.apkg";

  // LANGUAGES
  public static String ENGLISH = "English";
  public static String FRENCH = "French";
  public static String VIETNAMESE = "Vietnamese";
  public static String CHINESE_TD = "Chinese (Traditional)";
  public static String CHINESE_SP = "Chinese (Simplified)";
  public static String JAPANESE = "Japanese";
  public static String SPANISH = "Spanish";

  // CONSTANTS
  public static String TAB = "\t";

  // OXFORD
  public static String DICT_OXFORD_SPELLING_WRONG = "Did you spell it correctly?";
  public static String DICT_OXFORD_WORD_NOT_FOUND = "Oxford Learner's Dictionaries | Find the meanings";
  public static String DICT_OXFORD_URL_EN_EN = "https://www.oxfordlearnersdictionaries.com/search/english/direct/?q=%1$s";

  // LACVIET
  public static String DICT_LACVIET_SPELLING_WRONG = "Dữ liệu đang được cập nhật";
  public static String DICT_LACVIET_URL_VN_EN = "http://tratu.coviet.vn/tu-dien-lac-viet.aspx?learn=hoc-tieng-anh&t=V-A&k=%1$s";
  public static String DICT_LACVIET_URL_VN_FR = "http://tratu.coviet.vn/tu-dien-lac-viet.aspx?learn=hoc-tieng-phap&t=V-F&k=%1$s";
  public static String DICT_LACVIET_URL_EN_VN = "http://tratu.coviet.vn/tu-dien-lac-viet.aspx?learn=hoc-tieng-anh&t=A-V&k=%1$s";
  public static String DICT_LACVIET_URL_FR_VN = "http://tratu.coviet.vn/tu-dien-lac-viet.aspx?learn=hoc-tieng-phap&t=F-V&k=%1$s";

  // CAMBRIDGE
  public static String DICT_CAMBRIDGE_SPELLING_WRONG = "Did you spell it correctly?";
  public static String DICT_CAMBRIDGE_URL_EN_CN_TD = "https://dictionary.cambridge.org/search/english-chinese-traditional/direct/?q=%1$s";
  public static String DICT_CAMBRIDGE_URL_EN_CN_SP = "https://dictionary.cambridge.org/search/english-chinese-simplified/direct/?q=%1$s";
  public static String DICT_CAMBRIDGE_URL_EN_FR = "https://dictionary.cambridge.org/search/english-french/direct/?q=%1$s";
  public static String DICT_CAMBRIDGE_URL_EN_JP = "https://dictionary.cambridge.org/search/english-japanese/direct/?q=%1$s";

  // COLLINS
  public static String DICT_COLLINS_SPELLING_WRONG = "Sorry, no results for";
  public static String DICT_COLLINS_URL_FR_EN = "https://www.collinsdictionary.com/search/?dictCode=french-english&q=%1$s";

  // JDICT
  public static String DICT_JDICT_URL_VN_JP_OR_JP_VN = "https://j-dict.com/postrequest.ashx";

  // JISHO
  public static String DICT_JISHO_WORD_NOT_FOUND = "Sorry, couldn't find anything matching";
  public static String DICT_JISHO_WORD_URL_JP_EN = "https://jisho.org/word/%1$s";
  public static String DICT_JISHO_SEARCH_URL_JP_EN = "https://jisho.org/search/%1$s";

  // WORD REFERENCE
  public static String DICT_WORD_REFERENCE_SPELLING_WRONG = "";
  public static String DICT_WORD_REFERENCE_URL_EN_SP = "";
  public static String DICT_WORD_REFERENCE_URL_SP_EN = "";

  // OTHERS
  public static String DICT_NO_EXAMPLE = "";
  public static String DICT_SUCCESS = "Success";
  public static String DICT_COPYRIGHT = "This card's content is collected from the following dictionaries: %1$s";
  public static String DICT_WORD_NOT_FOUND = "The word was not found! Could you please check spelling or report to us!";
  public static String DICT_CONNECTION_FAILED = "Cannot connect to dictionaries, please try again later!";
  public static String DICT_NOT_SUPPORTED_TRANSLATION = "The translation from %1$s to %2$s is not supported!";
}
