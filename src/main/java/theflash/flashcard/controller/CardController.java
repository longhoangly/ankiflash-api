package theflash.flashcard.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import theflash.flashcard.payload.Card;
import theflash.flashcard.utils.Translation;

@RestController
@RequestMapping("/api/theflash")
public class CardController {

  @GetMapping("/v1/{word}/{source}/{target}/card")
  public Card generateFlashcard(@PathVariable String word, @PathVariable String source,
      @PathVariable String target) {
    Translation translation = new Translation(source, target);
    return new Card(word, translation);
  }
}
