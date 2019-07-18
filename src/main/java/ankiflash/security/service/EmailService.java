package ankiflash.security.service;

import ankiflash.security.dto.User;
import org.springframework.stereotype.Service;

@Service
public interface EmailService {

  void sendSimpleMessage(String to, String subject, String text);

  void sendVerificationEmail(User user);

  void sendResetPasswordEmail(User user);
}
