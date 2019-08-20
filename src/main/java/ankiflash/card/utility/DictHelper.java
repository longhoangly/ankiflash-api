package ankiflash.card.utility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DictHelper {

  private static final Logger logger = LoggerFactory.getLogger(DictHelper.class);

  public static String getLastElement(String link) {

    String[] link_els = link.split("/");
    return link_els[link_els.length - 1];
  }
}
