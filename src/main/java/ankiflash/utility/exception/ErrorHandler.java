package ankiflash.utility.exception;

import ankiflash.security.service.EmailService;
import ankiflash.security.service.impl.EmailServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ErrorHandler {

  private static final Logger logger = LoggerFactory.getLogger(ErrorHandler.class);

  private static final EmailService emailService = new EmailServiceImpl();

  public static void error(String msg, Throwable err) {
    emailService.sendExceptionEmail(err);
    logger.error(msg, err);
  }
}
