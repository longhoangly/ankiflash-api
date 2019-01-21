package theflash.anonymous.controller;

import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/anonymous")
public class AnonymousController {

  private static final Logger logger = LoggerFactory.getLogger(AnonymousController.class);

  @GetMapping("/test")
  public ResponseEntity test() {

    logger.info("/api/anonymous/test");
    return ResponseEntity.ok().body("Sent request successfully!");
  }
}
