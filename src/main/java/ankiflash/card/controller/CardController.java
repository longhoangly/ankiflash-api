package ankiflash.card.controller;

import ankiflash.card.dto.Card;
import ankiflash.card.payload.CardRequest;
import ankiflash.card.service.CardService;
import ankiflash.card.service.impl.card.ChineseCardServiceImpl;
import ankiflash.card.service.impl.card.EnglishCardServiceImpl;
import ankiflash.card.service.impl.card.FrenchCardServiceImpl;
import ankiflash.card.service.impl.card.JapaneseCardServiceImpl;
import ankiflash.card.service.impl.card.SpanishCardServiceImpl;
import ankiflash.card.service.impl.card.VietnameseCardServiceImpl;
import ankiflash.card.utility.Constants;
import ankiflash.card.utility.Status;
import ankiflash.card.utility.Translation;
import ankiflash.security.service.UserService;
import ankiflash.utility.AnkiFlashProps;
import ankiflash.utility.IOUtility;
import ankiflash.utility.exception.BadRequestException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/anki-flash-card")
class CardController {

  private static final Logger logger = LoggerFactory.getLogger(CardController.class);

  @Autowired private UserService userService;

  private CardService cardService;

  private String delimiter = ";";

  @PostMapping("/get-words")
  public ResponseEntity getJapaneseWords(@RequestBody @Valid CardRequest reqCard) {

    logger.info("/api/v1/anki-flash-card/get-words");

    String username = userService.getCurrentUsername();
    if (username.isEmpty()) {
      throw new BadRequestException("Cannot find your user info!!!");
    }

    // Initialize CardService
    cardService = getCardService(reqCard.getSource());

    // Get request info
    String[] words = reqCard.getWords().split(delimiter);
    Translation translation = new Translation(reqCard.getSource(), reqCard.getTarget());

    // Get all matched words
    List<String> jdWords = new ArrayList<>();
    for (String word : words) {
      jdWords.addAll(cardService.getWords(word, translation));
    }

    // Special pre-process for Japanese
    if (jdWords.isEmpty()) {
      throw new BadRequestException("Words not found. Please check your input!");
    }

    return ResponseEntity.ok().body(jdWords);
  }

  @PostMapping("/generate-card")
  public ResponseEntity generateCard(@RequestBody @Valid CardRequest reqCard) {

    logger.info("/api/v1/anki-flash-card/generate-card");

    String username = userService.getCurrentUsername();
    if (username.isEmpty()) {
      throw new BadRequestException("Cannot find your user info!!!");
    }

    // Initialize cardService
    cardService = getCardService(reqCard.getSource());

    // Get request info
    Translation translation = new Translation(reqCard.getSource(), reqCard.getTarget());
    String word = reqCard.getWords();

    // Generate card
    Card card = cardService.generateCard(word, translation, "dummy_request");
    return ResponseEntity.ok().body(card);
  }

  @PostMapping("/generate-cards")
  public ResponseEntity generateCards(@RequestBody @Valid CardRequest reqCard) {

    logger.info("/api/v1/anki-flash-card/generate-cards");

    String username = userService.getCurrentUsername();
    if (username.isEmpty()) {
      throw new BadRequestException("Cannot find your user info!!!");
    }

    // Initialize CardService
    cardService = getCardService(reqCard.getSource());

    // Create AnkiFlashcards per User
    String ankiDir =
        Paths.get(
                AnkiFlashProps.PARENT_ANKI_FLASH_DIR,
                username,
                reqCard.getSessionId(),
                AnkiFlashProps.SUB_ANKI_FLASH_DIR)
            .toString();
    IOUtility.createDirs(ankiDir);

    // Get request info
    List<String> words = Arrays.asList(reqCard.getWords().split(delimiter));
    Translation translation = new Translation(reqCard.getSource(), reqCard.getTarget());

    // Generate cards
    List<Card> cards = cardService.generateCards(words, translation, ankiDir);
    for (Card card : cards) {
      if (card.getStatus().compareTo(Status.Success) == 0) {
        IOUtility.write(ankiDir + "/" + Constants.ANKI_DECK, card.getContent());
      } else {
        IOUtility.write(
            ankiDir + "/" + Constants.ANKI_FAILURE,
            card.getWord() + " => " + card.getStatus() + "\n");
      }
    }

    return ResponseEntity.ok().body(cards);
  }

  @GetMapping(path = "/get-supported-language")
  public ResponseEntity getSupportedLanguagues() {

    logger.info("/api/v1/anki-flash-card/get-supported-language");

    cardService = new EnglishCardServiceImpl();
    List<Translation> languages = cardService.getSupportedLanguages();

    return ResponseEntity.ok().body(languages);
  }

  @GetMapping(path = "/download")
  public ResponseEntity download(@RequestParam(value = "sessionId") String sessionId)
      throws IOException {

    logger.info("/api/v1/anki-flash-card/download");

    cardService = new EnglishCardServiceImpl();
    String username = userService.getCurrentUsername();
    String ankiDir =
        Paths.get(
                AnkiFlashProps.PARENT_ANKI_FLASH_DIR,
                username,
                sessionId,
                AnkiFlashProps.SUB_ANKI_FLASH_DIR)
            .toString();
    String zipFilePath = cardService.compressResources(ankiDir);

    File zipFile = new File(zipFilePath);
    Path path = Paths.get(zipFile.getAbsolutePath());
    ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

    return ResponseEntity.ok()
        .contentLength(zipFile.length())
        .contentType(MediaType.parseMediaType("application/octet-stream"))
        .header(
            HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=\"" + resource.getFilename() + "\"")
        .body(resource);
  }

  @GetMapping(path = "/clean-up")
  public ResponseEntity cleanUp() {

    logger.info("/api/v1/anki-flash-card/clean-up");

    String username = userService.getCurrentUsername();
    String ankiDir = Paths.get(AnkiFlashProps.PARENT_ANKI_FLASH_DIR, username).toString();
    IOUtility.clean(ankiDir);

    return ResponseEntity.ok().body("Clean up successfully");
  }

  private CardService getCardService(String sourceLanguage) {

    CardService cardService;
    if (sourceLanguage.equalsIgnoreCase(Constants.ENGLISH)) {
      cardService = new EnglishCardServiceImpl();
    } else if (sourceLanguage.equalsIgnoreCase(Constants.VIETNAMESE)) {
      cardService = new VietnameseCardServiceImpl();
    } else if (sourceLanguage.equalsIgnoreCase(Constants.FRENCH)) {
      cardService = new FrenchCardServiceImpl();
    } else if (sourceLanguage.equalsIgnoreCase(Constants.CHINESE_TD)
        || sourceLanguage.equalsIgnoreCase(Constants.CHINESE_SP)) {
      cardService = new ChineseCardServiceImpl();
    } else if (sourceLanguage.equalsIgnoreCase(Constants.JAPANESE)) {
      cardService = new JapaneseCardServiceImpl();
    } else if (sourceLanguage.equalsIgnoreCase(Constants.SPANISH)) {
      cardService = new SpanishCardServiceImpl();
    } else {
      throw new BadRequestException(
          String.format("The language [%s] is not supported!", sourceLanguage));
    }

    return cardService;
  }
}
