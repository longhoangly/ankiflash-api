package ankiflash.utility.exception;

import ankiflash.security.service.EmailService;
import ankiflash.security.service.impl.EmailServiceImpl;
import java.io.PrintWriter;
import java.io.StringWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ErrorHandler {

  private static final Logger logger = LoggerFactory.getLogger(ErrorHandler.class);

  private static final EmailService emailService = new EmailServiceImpl();

  public static void log(String msg, Throwable err) {
    emailService.sendExceptionEmail(getStackTrace(err));
    logger.error(msg, err);
  }

  public static void log(Throwable err) {
    emailService.sendExceptionEmail(getStackTrace(err));
    logger.error("Exception Occurred", err);
  }

  public static String getStackTrace(final Throwable throwable) {
    final StringWriter sw = new StringWriter();
    final PrintWriter pw = new PrintWriter(sw, true);
    throwable.printStackTrace(pw);
    return sw.getBuffer().toString();
  }
}
