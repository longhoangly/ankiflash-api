package theflash.security.service.impl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;
import theflash.anonymous.controller.AnonymousController;
import theflash.helper.TheFlashProperties;
import theflash.security.dto.User;
import theflash.security.jwt.Generator;
import theflash.security.service.EmailService;

@Service
public class EmailServiceImpl implements EmailService {

  private static final Logger logger = LoggerFactory.getLogger(AnonymousController.class);

  @Autowired
  private Generator generator;

  private JavaMailSender mailSender;

  public EmailServiceImpl() {
    mailSender = getMailSender();
  }

  private JavaMailSender getMailSender() {

    JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
    try {
      mailSender.setHost(TheFlashProperties.MAIL_HOST);
      mailSender.setPort(TheFlashProperties.MAIL_PORT);
      mailSender.setUsername(TheFlashProperties.MAIL_USERNAME);
      mailSender.setPassword(TheFlashProperties.MAIL_PASSWORD);
      mailSender.setProtocol(TheFlashProperties.MAIL_PROTOCOL);

      Properties props = mailSender.getJavaMailProperties();
      props.put("mail.smtp.auth", TheFlashProperties.MAIL_AUTH);
      props.put("mail.smtp.starttls.enable", TheFlashProperties.MAIL_SSL);
      props.put("mail.debug", TheFlashProperties.MAIL_DEBUG);
    } catch (Exception e) {
      logger.error("Exception: ", e);
    }

    return mailSender;
  }

  @Override
  public void sendSimpleMessage(String to, String subject, String msg) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setTo(to);
    message.setSubject(subject);
    message.setText(msg);
    mailSender.send(message);
  }

  @Override
  public void sendVerificationEmail(User user) {

    URL relativeUrl = null;
    try {
      URL baseUrl = new URL(TheFlashProperties.API_SERVER_URL);
      relativeUrl = new URL(baseUrl,
          String.format("/api/auth/verify?key=%1$s", generator.generate(user, 24 * 60 * 60)));
    } catch (MalformedURLException e) {
      logger.info("MalformedURLException: ", e);
    }

    String emailTitle = "TheFlash Verify Email Address!";
    String emailContent = new StringBuilder()
        .append("Hi %1$s,\n")
        .append("\n")
        .append("Welcome to TheFlash!\n")
        .append("\n")
        .append("Please click on the link below for email verification.\n")
        .append("\n")
        .append("%2$s\n")
        .append("\n")
        .append("The link will be expired in 24 hours.\n")
        .append("\n")
        .append("If the above link does not work, copy and paste the link manually into your browser.\n")
        .append("\n")
        .append("Thanks for using our site!\n")
        .append("\n")
        .append("Thanks,\n")
        .append("TheFlash Team\n")
        .toString();

    emailContent = String.format(emailContent, user.getUsername(), relativeUrl.toString());
    sendSimpleMessage(user.getEmail(), emailTitle, emailContent);
  }

  @Override
  public void sendResetPasswordEmail(User user) {

    URL relativeUrl = null;
    try {
      URL baseUrl = new URL(TheFlashProperties.WEB_SERVER_URL);
      relativeUrl = new URL(baseUrl, String.format("/reset-password?key=%1$s", generator.generate(user, 20 * 60)));
    } catch (MalformedURLException e) {
      logger.error("MalformedURLException: ", e);
    }

    String emailTitle = "TheFlash Password Reset!";
    String emailContent = new StringBuilder()
        .append("Hi %1$s,\n")
        .append("\n")
        .append(
            "You're receiving this email because you requested a password reset for your user account at TheFlash.\n")
        .append("\n")
        .append("Please go to the following page and choose a new password.\n")
        .append("\n")
        .append("%2$s\n")
        .append("\n")
        .append("The link will be expired in 20 minutes.\n")
        .append("\n")
        .append("If the above link does not work, copy and paste the link manually into your browser.\n")
        .append("\n")
        .append("Thanks for using our site!\n")
        .append("\n")
        .append("Thanks,\n")
        .append("TheFlash Team\n")
        .toString();

    emailContent = String.format(emailContent, user.getUsername(), relativeUrl.toString());
    sendSimpleMessage(user.getEmail(), emailTitle, emailContent);
  }
}
