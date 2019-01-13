package theflash.security.service;

import theflash.security.dto.User;

public interface EmailService {

  void sendSimpleMessage(String to, String subject, String text);

  void sendVerificationEmail(User user);

  void sendResetPasswordEmail(User user);
}
