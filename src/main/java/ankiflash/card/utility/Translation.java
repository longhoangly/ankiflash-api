package ankiflash.card.utility;

public class Translation {

  public static final Translation EN_EN = new Translation(Constant.ENGLISH, Constant.ENGLISH);
  public static final Translation EN_VN = new Translation(Constant.ENGLISH, Constant.VIETNAMESE);
  public static final Translation EN_CN_TD = new Translation(Constant.ENGLISH, Constant.CHINESE_TD);
  public static final Translation EN_CN_SP = new Translation(Constant.ENGLISH, Constant.CHINESE_SP);
  public static final Translation EN_FR = new Translation(Constant.ENGLISH, Constant.FRENCH);
  public static final Translation EN_JP = new Translation(Constant.ENGLISH, Constant.JAPANESE);

  public static final Translation VN_EN = new Translation(Constant.VIETNAMESE, Constant.ENGLISH);
  public static final Translation VN_FR = new Translation(Constant.VIETNAMESE, Constant.FRENCH);
  public static final Translation VN_JP = new Translation(Constant.VIETNAMESE, Constant.JAPANESE);

  public static final Translation FR_VN = new Translation(Constant.FRENCH, Constant.VIETNAMESE);
  public static final Translation FR_EN = new Translation(Constant.FRENCH, Constant.ENGLISH);

  public static final Translation JP_EN = new Translation(Constant.JAPANESE, Constant.ENGLISH);
  public static final Translation JP_VN = new Translation(Constant.JAPANESE, Constant.VIETNAMESE);

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
    return this.source.equalsIgnoreCase(translation.getSource())
        && this.target.equalsIgnoreCase(translation.getTarget());
  }

  public String toString() {
    return this.source + "-" + this.target;
  }
}
