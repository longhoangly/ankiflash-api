package ankiflash.security.service.impl;

import ankiflash.security.dto.User;
import ankiflash.security.service.EmailService;
import ankiflash.security.service.UserService;
import ankiflash.security.utility.jwt.Generator;
import ankiflash.utility.AnkiFlashProps;
import ankiflash.utility.exception.ErrorHandler;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@DependsOn({"ankiFlashProps"})
public class EmailServiceImpl implements EmailService {

  private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

  @Autowired private Generator generator;

  @Autowired private UserService userService;

  private final JavaMailSender mailSender;

  public EmailServiceImpl() {
    mailSender = getMailSender();
  }

  private JavaMailSender getMailSender() {

    JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
    try {
      logger.info("MAIL_HOST=" + AnkiFlashProps.MAIL_HOST);
      logger.info("MAIL_PORT=" + AnkiFlashProps.MAIL_PORT);
      logger.info("MAIL_USERNAME=" + AnkiFlashProps.MAIL_USERNAME);
      logger.info("MAIL_PASSWORD=" + AnkiFlashProps.MAIL_PASSWORD);
      logger.info("MAIL_PROTOCOL=" + AnkiFlashProps.MAIL_PROTOCOL);
      logger.info("MAIL_AUTH=" + AnkiFlashProps.MAIL_AUTH);
      logger.info("MAIL_SSL=" + AnkiFlashProps.MAIL_SSL);
      logger.info("MAIL_DEBUG=" + AnkiFlashProps.MAIL_DEBUG);

      mailSender.setHost(AnkiFlashProps.MAIL_HOST);
      mailSender.setPort(AnkiFlashProps.MAIL_PORT);
      mailSender.setUsername(AnkiFlashProps.MAIL_USERNAME);
      mailSender.setPassword(AnkiFlashProps.MAIL_PASSWORD);
      mailSender.setProtocol(AnkiFlashProps.MAIL_PROTOCOL);

      Properties props = mailSender.getJavaMailProperties();
      props.put("mail.smtp.auth", AnkiFlashProps.MAIL_AUTH);
      props.put("mail.smtp.starttls.enable", AnkiFlashProps.MAIL_SSL);
      props.put("mail.debug", AnkiFlashProps.MAIL_DEBUG);
    } catch (java.lang.Exception e) {
      ErrorHandler.log(e);
    }

    return mailSender;
  }

  @Override
  public void sendSimpleMessage(String to, String subject, String msg) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setTo(to);
    message.setSubject(subject);
    message.setText(msg);
    message.setFrom(AnkiFlashProps.MAIL_FROM);
    mailSender.send(message);
  }

  @Override
  @Async
  public void sendVerificationEmail(User user) {

    String emailTitle = "AnkiFlash Verify Email Address!";
    String emailContent =
        "Hi %1$s,\n"
            + "\n"
            + "Welcome to AnkiFlash!\n"
            + "\n"
            + "Please click on the link below for email verification.\n"
            + "\n"
            + "%2$s\n"
            + "\n"
            + "The link will be expired in 24 hours.\n"
            + "\n"
            + "If the above link does not work, copy and paste the link manually into your browser.\n"
            + "\n"
            + "Thanks for using our site!\n"
            + "\n"
            + "Thanks,\n"
            + "AnkiFlash Team\n";

    String token24h = generator.generate(user, 24 * 60 * 60);
    String verificationUrl =
        String.format(
            "%1$s/api/auth/verify-email-address?key=%2$s", AnkiFlashProps.API_SERVER_URL, token24h);
    emailContent = String.format(emailContent, user.getUsername(), verificationUrl);
    sendSimpleMessage(user.getEmail(), emailTitle, emailContent);

    user.setToken(token24h);
    userService.update(user);
  }

  @Override
  @Async
  public void sendResetPasswordEmail(User user) {

    String emailTitle = "AnkiFlash Password Reset!";
    String emailContent =
        "Hi %1$s,\n"
            + "\n"
            + "You're receiving this email because you requested a password reset for your user account at AnkiFlash.\n"
            + "\n"
            + "Please go to the following page and choose a new password.\n"
            + "\n"
            + "%2$s\n"
            + "\n"
            + "The link will be expired in 20 minutes.\n"
            + "\n"
            + "If the above link does not work, copy and paste the link manually into your browser.\n"
            + "\n"
            + "Thanks for using our site!\n"
            + "\n"
            + "Thanks,\n"
            + "AnkiFlash Team\n";

    String token20m = generator.generate(user, 20 * 60);
    String resetUrl =
        String.format("%1$s/reset-password?key=%2$s", AnkiFlashProps.WEB_SERVER_URL, token20m);
    emailContent = String.format(emailContent, user.getUsername(), resetUrl);
    sendSimpleMessage(user.getEmail(), emailTitle, emailContent);

    user.setToken(token20m);
    userService.update(user);
  }

  @Override
  @Async
  public void sendExceptionEmail(String stackTrace) {

    String emailTitle = "URGENT!!! AnkiFlash ErrorHandler Occurred!!!";
    String emailContent =
        "Hi Admin,\n"
            + "\n"
            + "You received this email because of your admin role for AnkiFlash.\n"
            + "\n"
            + "Please check the log of the system to see if anything went wrong?\n"
            + "\n"
            + "The following error occurred!\n"
            + "\n"
            + "%s\n"
            + "\n"
            + "Thanks for taking care of our site!\n"
            + "\n"
            + "Thanks,\n"
            + "AnkiFlash Team\n";

    emailContent = String.format(emailContent, stackTrace);
    sendSimpleMessage(AnkiFlashProps.ADMIN_EMAIL, emailTitle, emailContent);
  }
}
