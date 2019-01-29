package theflash.flashcard.utils;

import java.util.Map;

public class Translation implements Map.Entry<String, String> {

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

  private String source;
  private String target;

  /**
   * Creates a new translation
   *
   * @param source The source language for this translation
   * @param target The target language for this translation
   */
  public Translation(String source, String target) {
    this.source = source;
    this.target = target;
  }

  @Override
  public String getKey() {
    return source;
  }

  @Override
  public String getValue() {
    return target;
  }

  @Override
  public String setValue(String target) {
    return this.target = target;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Translation translation = (Translation) o;
    return source.equals(translation.getKey()) && target.equals(translation.getValue());
  }
}
