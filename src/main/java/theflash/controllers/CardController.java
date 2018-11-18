package theflash.controllers;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import theflash.dto.Card;
import theflash.handlers.utility.Translation;

@RestController
public class CardController {

  @RequestMapping(value = "/theflash/api/v1/{word}/{source}/{target}/card", method = RequestMethod.GET)
  public Card generateFlashcard(@PathVariable String word, @PathVariable String source,
      @PathVariable String target) {
    Translation translation = new Translation(source, target);
    return new Card(word, translation);
  }
}
