package ankiflash.utility;

import ankiflash.utility.exception.ErrorHandler;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IOUtility {

  private static final Logger logger = LoggerFactory.getLogger(IOUtility.class);

  public static void clean(String dirPath) {

    File dir = new File(dirPath);
    try {
      if (dir.exists()) {
        FileUtils.deleteDirectory(dir);
        logger.info("Directory deleted, {}", dirPath);
      }
    } catch (IOException e) {
      ErrorHandler.log(e);
    }
  }

  public static void removeFile(String dirPath) {

    Path path = Paths.get(dirPath);
    try {
      if (Files.deleteIfExists(path)) {
        logger.info("Deleted the file, {}", path.getRoot());
      } else {
        logger.info("Failed to delete the file.");
      }
    } catch (IOException e) {
      ErrorHandler.log(e);
    }
  }

  public static void write(String filePath, String content) {

    File file = new File(filePath);
    try {
      if (!file.exists()) {
        file.createNewFile();
      }
      Files.write(Paths.get(filePath), content.getBytes(), StandardOpenOption.APPEND);
    } catch (IOException e) {
      ErrorHandler.log(e);
    }
  }

  public static void createDirs(String dirPath) {

    File dir = new File(dirPath);
    if (!dir.exists()) {
      if (dir.mkdirs()) {
        logger.info("Directory created, {}", dirPath);
      } else {
        logger.error("Directory creation failed, {}", dirPath);
      }
    } else {
      logger.info("Directory already existed, {}", dirPath);
    }
  }

  public static void copyFolder(String srcPath, String desPath) {

    Path src = Paths.get(srcPath);
    Path dest = Paths.get(desPath);
    try {
      Files.walk(src)
          .forEach(
              source -> {
                Path destination = dest.resolve(src.relativize(source));
                if (Files.isDirectory(source)) {
                  if (!Files.exists(destination)) {
                    createDirs(destination.toString());
                  }
                  return;
                }
                copyFile(source.toString(), destination.toString());
              });
    } catch (IOException e) {
      ErrorHandler.log(e);
    }
  }

  public static void copyFile(String srcPath, String desPath) {

    try {
      Files.copy(
          Paths.get(srcPath),
          Paths.get(desPath),
          StandardCopyOption.COPY_ATTRIBUTES,
          StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      ErrorHandler.log(e);
    }
  }

  public static void zipFolder(String dirPath, String outputPath) {

    logger.info("Zipping directory, {}", dirPath);
    try (FileOutputStream fos = new FileOutputStream(outputPath)) {
      ZipOutputStream zipOut = new ZipOutputStream(fos);
      File fileToZip = new File(dirPath);

      zipFile(fileToZip, fileToZip.getName(), zipOut);
      zipOut.close();
    } catch (IOException e) {
      ErrorHandler.log(e);
    }
    logger.info("File zipped, {}", outputPath);
  }

  private static void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) {

    try {
      if (fileToZip.isHidden()) {
        return;
      }

      if (fileToZip.isDirectory()) {
        if (fileName.endsWith("/")) {
          zipOut.putNextEntry(new ZipEntry(fileName));
        } else {
          zipOut.putNextEntry(new ZipEntry(fileName + "/"));
        }
        zipOut.closeEntry();
        File[] children = fileToZip.listFiles();
        for (File childFile : Objects.requireNonNull(children)) {
          zipFile(childFile, fileName + "/" + childFile.getName(), zipOut);
        }
        return;
      }

      FileInputStream fis = new FileInputStream(fileToZip);
      ZipEntry zipEntry = new ZipEntry(fileName);
      zipOut.putNextEntry(zipEntry);
      byte[] bytes = new byte[1024];
      int length;
      while ((length = fis.read(bytes)) >= 0) {
        zipOut.write(bytes, 0, length);
      }
      fis.close();
    } catch (IOException e) {
      ErrorHandler.log(e);
    }
  }

  public static boolean download(String url, Path target) {
    if (!url.isEmpty()) {
      try {
        URL site = new URL(url);
        URLConnection connection;
        if (!AnkiFlashProps.PROXY_ADDRESS.isEmpty() && AnkiFlashProps.PROXY_PORT != 0) {
          Proxy proxy =
              new Proxy(
                  Type.HTTP,
                  new InetSocketAddress(AnkiFlashProps.PROXY_ADDRESS, AnkiFlashProps.PROXY_PORT));
          connection = site.openConnection(proxy);
        } else {
          connection = site.openConnection();
        }
        connection.addRequestProperty("User-Agent", "Mozilla/5.0 Gecko/20100101 Firefox/47.0");
        connection.setConnectTimeout(AnkiFlashProps.CONNECTION_TIMEOUT);
        connection.setReadTimeout(AnkiFlashProps.READ_TIMEOUT);

        if (!target.toFile().exists()) {
          InputStream in = connection.getInputStream();
          Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        } else {
          logger.warn("The file existed already! url={}, target = {}", url, target);
        }
      } catch (IOException e) {
        ErrorHandler.log(e);
        return false;
      }
    } else {
      logger.warn("The url was empty! url={}, target = {}", url, target);
    }

    return true;
  }
}
