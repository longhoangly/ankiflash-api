package theflash.handlers.utility;

import javafx.util.Pair;

public class Translation extends Pair {

  public static Translation EN_EN = new Translation(Constants.ENGLISH, Constants.ENGLISH);
  public static Translation EN_VN = new Translation(Constants.ENGLISH, Constants.VIETNAMESE);
  public static Translation EN_CN = new Translation(Constants.ENGLISH, Constants.CHINESE);

  public static Translation VN_EN = new Translation(Constants.VIETNAMESE, Constants.ENGLISH);
  public static Translation VN_FR = new Translation(Constants.VIETNAMESE, Constants.FRENCH);

  public static Translation FR_VN = new Translation(Constants.FRENCH, Constants.VIETNAMESE);
  public static Translation FR_EN = new Translation(Constants.FRENCH, Constants.ENGLISH);

  // ToDo: Going to implement the following
  // public static Translation VN_JP = new Translation(Constants.ENGLISH, Constants.ENGLISH);
  // public static Translation JP_EN = new Translation(Constants.JAPANESE, Constants.ENGLISH);
  // public static Translation JP_VN = new Translation(Constants.JAPANESE, Constants.VIETNAMESE);


  /**
   * Creates a new pair
   *
   * @param source The key for this pair
   * @param target The value to use for this pair
   */
  public Translation(String source, String target) {
    super(source, target);
  }
}
