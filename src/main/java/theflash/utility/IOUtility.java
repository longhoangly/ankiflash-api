package theflash.utility;

import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IOUtility {

  private static final Logger logger = LoggerFactory.getLogger(IOUtility.class);

  public static boolean createDirs(String path) {
    File files = new File(path);
    if (!files.exists()) {
      if (files.mkdirs()) {
        logger.info(String.format("Multiple directories are created!, %s", path));
        return true;
      } else {
        logger.error(String.format("Failed to create multiple directories!, %s", path));
        return false;
      }
    }
    return true;
  }
}
