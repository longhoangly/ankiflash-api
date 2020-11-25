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

  public static void downloadFiles(String ankiDir, String urls) {

    String[] downloadLinks = urls.split(";");
    for (var link : downloadLinks) {
      String fileName = DictHelper.getFileName(link);
      File dir = new File(ankiDir);
      if (dir.exists() && !link.isEmpty()) {
        Path output = Paths.get(dir.getAbsolutePath(), fileName);
        IOUtility.download(link, output);
      } else {
        logger.warn("ankiDir={}, dir.exists={}, link={}", ankiDir, dir.exists(), link);
      }
    }
  }
}
