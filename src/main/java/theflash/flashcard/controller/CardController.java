package theflash.flashcard.controller;

import java.util.Arrays;
import java.util.List;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import theflash.flashcard.dto.Card;
import theflash.flashcard.payload.CardRequest;
import theflash.flashcard.service.CardService;
import theflash.flashcard.service.impl.card.EnglishCardServiceImpl;
import theflash.flashcard.utils.Constants;
import theflash.flashcard.utils.Translation;

@RestController
@RequestMapping("/api/the-flash")
public class CardController {

  private static final Logger logger = LoggerFactory.getLogger(CardController.class);

  @PostMapping("/v1/get-cards")
  public ResponseEntity generateFlashcard(@RequestBody @Valid CardRequest reqCard) {

    logger.info("/api/the-flash/v1/get-cards");
    Translation translation = new Translation(reqCard.getSource(), reqCard.getTarget());

    CardService cardService = null;
    if (translation.getSource().equals(Constants.ENGLISH)) {
      cardService = new EnglishCardServiceImpl();
    } else if (translation.getSource().equals(Constants.VIETNAMESE)) {
      //Initialise cardService with VietnameseCardServiceImpl
    } else if (translation.getSource().equals(Constants.CHINESE)) {
      //Initialise cardService with ChineseCardServiceImpl
    } else if (translation.getSource().equals(Constants.JAPANESE)) {
      //Initialise cardService with JapaneseCardServiceImpl
    } else if (translation.getSource().equals(Constants.SPANISH)) {
      //Initialise cardService with SpanishCardServiceImpl
    }

    List<Card> cards = cardService.generateCards(Arrays.asList(reqCard.getWordList().split(";")), translation);
    return ResponseEntity.ok().body(cards);
  }
}
