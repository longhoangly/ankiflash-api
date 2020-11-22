package ankiflash.card.utility;

import ankiflash.utility.IOUtility;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DictHelper {

  private static final Logger logger = LoggerFactory.getLogger(DictHelper.class);

  public static String getFileName(String link) {

    String[] link_els = link.split("/");
    return link_els[link_els.length - 1];
  }

  public static void downloadFile(String ankiDir, String url) {

    String fileName = DictHelper.getFileName(url);
    File dir = new File(ankiDir);
    if (dir.exists() && !url.isEmpty()) {
      Path output = Paths.get(dir.getAbsolutePath(), fileName);
      IOUtility.download(url, output);
    } else {
      logger.warn("ankiDir={}, dir.exists={}, url={}", ankiDir, dir.exists(), url);
    }
  }
}
