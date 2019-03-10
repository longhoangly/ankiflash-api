package theflash.flashcard.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import theflash.flashcard.dto.Card;
import theflash.flashcard.payload.CardRequest;
import theflash.flashcard.service.CardService;
import theflash.flashcard.service.impl.card.CardServiceImpl;
import theflash.flashcard.service.impl.card.EnglishCardServiceImpl;
import theflash.flashcard.utils.Status;
import theflash.flashcard.utils.Translation;
import theflash.security.service.UserService;
import theflash.utility.IOUtility;
import theflash.utility.TheFlashProperties;
import theflash.utility.exception.BadRequestException;

@RestController
@RequestMapping("/api/v1/anki-flash-card")
public class CardController {

  private static final Logger logger = LoggerFactory.getLogger(CardController.class);

  @Autowired
  private UserService userService;

  private CardService cardService;

  @PostMapping("/generate-card")
  public ResponseEntity generateCard(@RequestBody @Valid CardRequest reqCard) {

    logger.info("/api/v1/anki-flash-card/generate-card");

    String username = userService.getCurrentUsername();
    if (username.isEmpty()) {
      throw new BadRequestException("Cannot find your user info!!!");
    }

    // Initialize CardService per Translation
    Translation translation = new Translation(reqCard.getSource(), reqCard.getTarget());
    cardService = CardServiceImpl.getCardService(translation);

    // Generate Cards
    Card card = cardService.generateCard(reqCard.getWords(), translation, username);
    return ResponseEntity.ok().body(card);
  }

  @PostMapping("/generate-cards")
  public ResponseEntity generateCards(@RequestBody @Valid CardRequest reqCard) {

    logger.info("/api/v1/anki-flash-card/generate-cards");

    String username = userService.getCurrentUsername();
    if (username.isEmpty()) {
      throw new BadRequestException("Cannot find your user info!!!");
    }

    // Initialize CardService per Translation
    Translation translation = new Translation(reqCard.getSource(), reqCard.getTarget());
    cardService = CardServiceImpl.getCardService(translation);

    // Create AnkiFlashcards per User
    String ankiDir = Paths.get(username, TheFlashProperties.ANKI_DIR_FLASHCARDS).toString();
    IOUtility.clean(ankiDir);
    IOUtility.createDirs(ankiDir);

    // Generate Cards
    List<String> words = Arrays.asList(reqCard.getWords().split(";"));
    List<Card> cards = cardService.generateCards(words, translation, username);
    for (Card card : cards) {
      if (card.getStatus().compareTo(Status.SUCCESS) == 0) {
        IOUtility.write(ankiDir + "/cards.csv", card.getContent());
      } else {
        IOUtility.write(ankiDir + "/failed_words.csv", card.getWord() + " => " + card.getStatus() + "\n");
      }
    }

    return ResponseEntity.ok().body(cards);
  }

  @GetMapping(path = "/download")
  public ResponseEntity<Resource> download() throws IOException {

    logger.info("/api/v1/anki-flash-card/download");

    cardService = new EnglishCardServiceImpl();
    String filePath = cardService.compressResources(userService.getCurrentUsername());

    File file = new File(filePath);
    HttpHeaders headers = new HttpHeaders();
    headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
    headers.add("Pragma", "no-cache");
    headers.add("Expires", "0");

    Path path = Paths.get(file.getAbsolutePath());
    ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

    return ResponseEntity.ok()
                         .headers(headers)
                         .contentLength(file.length())
                         .contentType(MediaType.parseMediaType("application/octet-stream"))
                         .body(resource);
  }
}
