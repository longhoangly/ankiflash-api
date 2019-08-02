package ankiflash.security.service;

import ankiflash.security.dto.User;
import org.springframework.stereotype.Service;

@Service
public interface SocialAuthService {

  User googleVerify(String idTokenString);

  User facebookVerify(String accessToken);
}
