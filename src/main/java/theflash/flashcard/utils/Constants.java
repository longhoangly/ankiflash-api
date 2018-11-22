package theflash.flashcard.utils;

import java.nio.file.Paths;
import theflash.helper.PropertiesHelper;

public class Constants {
  // OXFORD
  public static String DICT_OXFORD_INTERFACE_CSS = "interface.css";
  public static String DICT_OXFORD_CSS = "oxford.css";
  public static String DICT_OXFORD_RESPONSIVE_CSS = "responsive.css";
  public static String DICT_OXFORD_BTN_WORDLIST_PNG = "btn-wordlist.png";
  public static String DICT_OXFORD_USONLY_AUDIO_PNG = "usonly-audio.png";
  public static String DICT_OXFORD_ENLARGE_IMG_PNG = "enlarge-img.png";
  public static String DICT_OXFORD_ENTRY_ARROW_PNG = "entry-arrow.png";
  public static String DICT_OXFORD_ENTRY_BULLET_PNG = "entry-bullet.png";
  public static String DICT_OXFORD_ENTRY_SQBULLET_PNG = "entry-sqbullet.png";
  public static String DICT_OXFORD_GO_TO_TOP_PNG = "go-to-top.png";
  public static String DICT_OXFORD_ICON_ACADEMIC_PNG = "icon-academic.png";
  public static String DICT_OXFORD_ICON_AUDIO_BRE_PNG = "icon-audio-bre.png";
  public static String DICT_OXFORD_ICON_AUDIO_NAME_PNG = "icon-audio-name.png";
  public static String DICT_OXFORD_ICON_OX3000_PNG = "icon-ox3000.png";
  public static String DICT_OXFORD_ICON_PLUS_MINUS_PNG = "icon-plus-minus.png";
  public static String DICT_OXFORD_ICON_PLUS_MINUS_GREY_PNG = "icon-plus-minus-grey.png";
  public static String DICT_OXFORD_ICON_PLUS_MINUS_ORANGE_PNG = "icon-plus-minus-orange.png";
  public static String DICT_OXFORD_ICON_SELECT_ARROW_CIRRLE_BLUE_PNG = "icon-select-arrow-circle-blue.png";
  public static String DICT_OXFORD_LOGIN_BG_PNG = "login-bg.png";
  public static String DICT_OXFORD_PVARR_PNG = "pvarr.png";
  public static String DICT_OXFORD_PVARR_BLUE_PNG = "pvarr-blue.png";
  public static String DICT_OXFORD_SEARCH_MAG_PNG = "search-mag.png";
  // SOHA
  public static String DICT_SOHA_MAIN_MIN_CSS = "main_min.css";
  public static String DICT_SOHA_DOT_JPG = "dot.jpg";
  public static String DICT_SOHA_MINUS_SECTION_JPG = "minus_section.jpg";
  public static String DICT_SOHA_PLUS_SECTION_JPG = "plus_section.jpg";
  public static String DICT_SOHA_HIDDEN_JPG = "hidden.jpg";
  public static String DICT_SOHA_EXTERNAL_PNG = "external.png";
  // COLLINS
  public static String DICT_COLLINS_CSS = "collins_common.css";
  public static String DICT_COLLINS_ICONS_RIGHT_PNG = "icons-right.png";
  // LACVIET
  public static String DICT_LACVIET_HOME_CSS = "home.css";
  public static String DICT_LACVIET_ICON_6_7_PNG = "Icon_6_7.png";
  public static String DICT_LACVIET_ICON_7_4_PNG = "Icon_7_4.png";
  // CAMBRIDGE
  public static String DICT_CAMBRIDGE_COMMON_CSS = "common.css";
  public static String DICT_CAMBRIDGE_STAR_PNG = "star.png";
  // ANKI FILES
  public static String DICT_DECK = "ankiDeck.csv";
  public static String DICT_LANGUAGE = "Language.txt";
  public static String DICT_PNG = "anki.png";
  // ANKI MODELS
  public static String ANKI_EN_SINGLE_FORM_ABCDEFGHLONGLEE123 = "[EN]singleformABCDEFGHLONGLEE123.apkg";
  public static String ANKI_EN_MULTIPLE_FORM_ABCDEFGHLONGLEE123 = "[EN]multiformABCDEFGHLONGLEE123.apkg";
  public static String ANKI_FR_SINGLE_FORM_ABCDEFGHLONGLEE123 = "[FR]singleformABCDEFGHLONGLEE123.apkg";
  public static String ANKI_FR_MULTIPLE_FORM_ABCDEFGHLONGLEE123 = "[FR]multiformABCDEFGHLONGLEE123.apkg";
  public static String ANKI_VN_SINGLE_FORM_ABCDEFGHLONGLEE123 = "[VN]singleformABCDEFGHLONGLEE123.apkg";
  // ANKI DIRS
  public static String ANKI_DIR_OXFORD = Paths.get(PropertiesHelper.ANKI_DIR_FLASHCARDS, "oxlayout").toString();
  public static String ANKI_DIR_SOHA = Paths.get(PropertiesHelper.ANKI_DIR_FLASHCARDS, "soha").toString();
  public static String ANKI_DIR_LACVIET = Paths.get(PropertiesHelper.ANKI_DIR_FLASHCARDS, "lacViet").toString();
  public static String ANKI_DIR_CAMBRIDGE = Paths.get(PropertiesHelper.ANKI_DIR_FLASHCARDS, "cambridge").toString();
  public static String ANKI_DIR_COLLINS = Paths.get(PropertiesHelper.ANKI_DIR_FLASHCARDS, "collins").toString();
  public static String ANKI_DIR_SOUND = Paths.get(PropertiesHelper.ANKI_DIR_FLASHCARDS, "sounds").toString();
  public static String ANKI_DIR_IMAGE = Paths.get(PropertiesHelper.ANKI_DIR_FLASHCARDS, "images").toString();
  // LANGUAGES
  public static String ENGLISH = "English";
  public static String FRENCH = "French";
  public static String VIETNAMESE = "Vietnamese";
  public static String CHINESE = "Chinese";
  public static String JAPANESE = "Japanese";
  public static String SPANISH = "Spanish";
  // CONSTANTS
  public static String TAB = "\t";
  public static String CR = "\r";
  public static String LF = "\n";
  // OXFORD
  public static String DICT_OXFORD_SPELLING_WRONG_1 = "Did you spell it correctly?";
  public static String DICT_OXFORD_SPELLING_WRONG_2 = "Oxford Learner's Dictionaries | Find the meanings";
  public static String DICT_OXFORD_URL_EN_EN = "http://www.oxfordlearnersdictionaries.com/search/english/direct/?q=%s";
  // LACVIET
  public static String DICT_LACVIET_SPELLING_WRONG = "Dữ liệu đang được cập nhật";
  public static String DICT_LACVIET_URL_VN_EN = "http://tratu.coviet.vn/tu-dien-lac-viet.aspx?learn=hoc-tieng-anh&t=V-A&k=%s";
  public static String DICT_LACVIET_URL_VN_FR = "http://tratu.coviet.vn/tu-dien-lac-viet.aspx?learn=hoc-tieng-phap&t=V-F&k=%s";
  public static String DICT_LACVIET_URL_EN_VN = "http://tratu.coviet.vn/tu-dien-lac-viet.aspx?learn=hoc-tieng-anh&t=A-V&k=%s";
  public static String DICT_LACVIET_URL_FR_VN = "http://tratu.coviet.vn/tu-dien-lac-viet.aspx?learn=hoc-tieng-phap&t=F-V&k=%s";
  // CAMBRIDGE
  public static String DICT_CAMBRIDGE_SPELLING_WRONG = "你拼写正确了吗？";
  public static String DICT_CAMBRIDGE_URL_EN_CN = "http://dictionary.cambridge.org/zhs/%E6%90%9C%E7%B4%A2/english-chinese-simplified/direct/?q=%s";
  // COLLINS
  public static String DICT_COLLINS_SPELLING_WRONG = "CollinsDictionary.com | Collins Dictionaries - Free Online";
  public static String DICT_COLLINS_URL_FR_EN = "https://www.collinsdictionary.com/search/?dictCode=french-english&q=%s";
  // OTHERS
  public static String DICT_COPYRIGHT = "This flashcard's content is collected from the following dictionaries: %s";
  public static String DICT_NO_EXAMPLE = "There is no example for this word!";
  public static String DICT_CONNECTION_FAILED = "Cannot connect to dictionaries, please try again later!";
  public static String DICT_WORD_NOT_FOUND = "The word not found! Could you please check spelling or report to us!";
}
