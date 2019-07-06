package theflash.security.service;

import org.springframework.stereotype.Service;
import theflash.security.dto.User;

@Service
public interface EmailService {

  void sendSimpleMessage(String to, String subject, String text);

  void sendVerificationEmail(User user);

  void sendResetPasswordEmail(User user);
}
