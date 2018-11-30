package theflash.flashcard.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import theflash.flashcard.payload.Card;
import theflash.flashcard.utils.Translation;

@RestController
@RequestMapping("/api/theflash")
public class CardController {

  private static final Logger logger = LoggerFactory.getLogger(CardController.class);

  @GetMapping("/v1/{word}/{source}/{target}/card")
  public Card generateFlashcard(@PathVariable String word, @PathVariable String source,
      @PathVariable String target) {

    logger.info("/api/theflash/v1/{word}/{source}/{target}/card");
    Translation translation = new Translation(source, target);
    return new Card(word, translation);
  }
}
