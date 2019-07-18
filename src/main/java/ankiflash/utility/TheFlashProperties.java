package ankiflash.utility;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TheFlashProperties {

  public static String API_SERVER_URL;
  public static String WEB_SERVER_URL;
  public static String PROXY_ADDRESS;
  public static int PROXY_PORT;
  public static String ANKI_DIR_FLASHCARDS;
  public static int CONNECTION_TIMEOUT;
  public static int READ_TIMEOUT;
  public static String MAIL_HOST;
  public static int MAIL_PORT;
  public static String MAIL_USERNAME;
  public static String MAIL_PASSWORD;
  public static String MAIL_PROTOCOL;
  public static boolean MAIL_AUTH;
  public static boolean MAIL_SSL;
  public static boolean MAIL_DEBUG;
  public static String MAIL_FROM;


  @Value("${server.api.url}")
  public void setApiServerUrl(String apiServerUrl) {
    API_SERVER_URL = apiServerUrl;
  }

  @Value("${server.web.url}")
  public void setWebServerUrl(String webServerUrl) {
    WEB_SERVER_URL = webServerUrl;
  }

  @Value("${ankiflash.proxy.address}")
  public void setProxyAddress(String proxyAddress) {
    PROXY_ADDRESS = proxyAddress;
  }

  @Value("${ankiflash.proxy.port}")
  public void setProxyPort(int proxyPort) {
    PROXY_PORT = proxyPort;
  }

  @Value("${ankiflash.anki.root}")
  public void setAnkiDirFlashcards(String ankiDirFlashcards) {
    ANKI_DIR_FLASHCARDS = ankiDirFlashcards;
  }

  @Value("${ankiflash.timeout.connection}")
  public void setConnectionTimeout(int timeout) {
    CONNECTION_TIMEOUT = timeout * 1000;
  }

  @Value("${ankiflash.timeout.read}")
  public void setReadTimeout(int timeout) {
    READ_TIMEOUT = timeout * 1000;
  }

  @Value("${spring.mail.host}")
  public void setMailHost(String mailHost) {
    MAIL_HOST = mailHost;
  }

  @Value("${spring.mail.port}")
  public void setMailPort(int mailPort) {
    MAIL_PORT = mailPort;
  }

  @Value("${spring.mail.username}")
  public void setMailUsername(String mailUsername) {
    MAIL_USERNAME = mailUsername;
  }

  @Value("${spring.mail.password}")
  public void setMailPassword(String mailPassword) {
    MAIL_PASSWORD = mailPassword;
  }

  @Value("${spring.mail.transport.protocol}")
  public void setMailProtocol(String mailProtocol) {
    MAIL_PROTOCOL = mailProtocol;
  }

  @Value("${spring.mail.smtp.auth}")
  public void setMailAuth(boolean mailAuth) {
    MAIL_AUTH = mailAuth;
  }

  @Value("${spring.mail.smtp.starttls.enable}")
  public void setMailSsl(boolean mailSsl) {
    MAIL_SSL = mailSsl;
  }

  @Value("${spring.mail.debug}")
  public void setMailDebug(boolean mailDebug) {
    MAIL_DEBUG = mailDebug;
  }

  @Value("${spring.mail.from}")
  public void setMailFrom(String mailFrom) {
    MAIL_FROM = mailFrom;
  }
}