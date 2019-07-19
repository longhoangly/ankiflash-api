package ankiflash.card.utility;

public class Translation {

  public static final Translation EN_EN = new Translation(Constants.ENGLISH, Constants.ENGLISH);
  public static final Translation EN_VN = new Translation(Constants.ENGLISH, Constants.VIETNAMESE);
  public static final Translation EN_CN_TD = new Translation(Constants.ENGLISH, Constants.CHINESE_TD);
  public static final Translation EN_CN_SP = new Translation(Constants.ENGLISH, Constants.CHINESE_SP);
  public static final Translation EN_FR = new Translation(Constants.ENGLISH, Constants.FRENCH);
  public static final Translation EN_JP = new Translation(Constants.ENGLISH, Constants.JAPANESE);

  public static final Translation VN_EN = new Translation(Constants.VIETNAMESE, Constants.ENGLISH);
  public static final Translation VN_FR = new Translation(Constants.VIETNAMESE, Constants.FRENCH);
  public static final Translation VN_JP = new Translation(Constants.VIETNAMESE, Constants.JAPANESE);

  public static final Translation FR_VN = new Translation(Constants.FRENCH, Constants.VIETNAMESE);
  public static final Translation FR_EN = new Translation(Constants.FRENCH, Constants.ENGLISH);

  public static final Translation JP_EN = new Translation(Constants.JAPANESE, Constants.ENGLISH);
  public static final Translation JP_VN = new Translation(Constants.JAPANESE, Constants.VIETNAMESE);

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

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public String getTarget() {
    return target;
  }

  public void setTarget(String target) {
    this.target = target;
  }

  public boolean equals(Translation translation) {
    return this.source.equalsIgnoreCase(translation.getSource()) && this.target
        .equalsIgnoreCase(translation.getTarget());
  }
}
