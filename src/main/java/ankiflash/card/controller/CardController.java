package ankiflash.card.controller;

import ankiflash.card.dto.Card;
import ankiflash.card.payload.CardRequest;
import ankiflash.card.payload.WordResponse;
import ankiflash.card.service.CardDbService;
import ankiflash.card.service.CardService;
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
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AuthorizationServiceException;
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

  @Autowired private CardDbService cardDbService;

  @Autowired private BeanFactory beans;

  private CardService cardService;

  private String delimiter = "%";

  @PostMapping("/get-words")
  public ResponseEntity getWords(@RequestBody @Valid CardRequest reqCard) {

    logger.info("/api/v1/anki-flash-card/get-words");

    String username = userService.getCurrentUsername();
    if (username.isEmpty()) {
      throw new AuthorizationServiceException("Unauthorized user!");
    }

    initializeCardService(reqCard.getSource());
    String[] words = reqCard.getWords().split(delimiter);
    Translation translation = new Translation(reqCard.getSource(), reqCard.getTarget());

    List<String> success = new ArrayList<>();
    List<String> failure = new ArrayList<>();
    for (String word : words) {
      List<String> matchedWords = cardService.getWords(word, translation);
      if (matchedWords.isEmpty()) {
        failure.add(word);
      } else {
        success.addAll(matchedWords);
      }
    }

    WordResponse resWords = new WordResponse();
    resWords.setSuccess(success);
    resWords.setFailure(failure);

    return ResponseEntity.ok().body(resWords);
  }

  @PostMapping("/generate-card")
  public ResponseEntity generateCard(@RequestBody @Valid CardRequest reqCard) {

    logger.info("/api/v1/anki-flash-card/generate-card");

    String username = userService.getCurrentUsername();
    if (username.isEmpty()) {
      throw new AuthorizationServiceException("Unauthorized user!");
    }

    initializeCardService(reqCard.getSource());

    // Get request info
    Translation translation = new Translation(reqCard.getSource(), reqCard.getTarget());
    String word = reqCard.getWords();

    // Generate card
    Card card = cardService.generateCard(word, translation, "", reqCard.getIsOffline());
    return ResponseEntity.ok().body(card);
  }

  @PostMapping("/generate-cards")
  public ResponseEntity generateCards(@RequestBody @Valid CardRequest reqCard) {

    logger.info("/api/v1/anki-flash-card/generate-cards");

    String username = userService.getCurrentUsername();
    if (username.isEmpty()) {
      throw new AuthorizationServiceException("Unauthorized user!");
    }

    initializeCardService(reqCard.getSource());

    // Get request info
    List<String> words = Arrays.asList(reqCard.getWords().split(delimiter));
    Translation translation = new Translation(reqCard.getSource(), reqCard.getTarget());

    // Create ankiDir folder
    String ankiDir =
        Paths.get(
                AnkiFlashProps.PARENT_ANKI_FLASH_DIR,
                username,
                reqCard.getSessionId(),
                AnkiFlashProps.SUB_ANKI_FLASH_DIR)
            .toString();
    IOUtility.createDirs(ankiDir);

    // Generate cards
    List<Card> cards =
        cardService.generateCards(words, translation, ankiDir, reqCard.getIsOffline());
    for (Card card : cards) {
      if (card.getStatus().compareTo(Status.Success) == 0) {
        String soundHtml = reqCard.getIsOffline() ? card.getSoundOffline() : card.getSoundOnline();
        String imageHtml = reqCard.getIsOffline() ? card.getImageOffline() : card.getImageOnline();
        String cardContent =
            card.getWord()
                + Constants.TAB
                + card.getWordType()
                + Constants.TAB
                + card.getPhonetic()
                + Constants.TAB
                + card.getExample()
                + Constants.TAB
                + soundHtml
                + Constants.TAB
                + imageHtml
                + Constants.TAB
                + card.getMeaning()
                + Constants.TAB
                + card.getCopyright()
                + Constants.TAB
                + card.getTag()
                + "\n";
        IOUtility.write(ankiDir + "/" + Constants.ANKI_DECK, cardContent);
      } else {
        IOUtility.write(
            ankiDir + "/" + Constants.ANKI_FAILURE,
            card.getWord() + " => " + card.getStatus() + ":" + card.getComment() + "\n");
      }
    }

    return ResponseEntity.ok().body(cards);
  }

  @GetMapping(path = "/get-supported-language")
  public ResponseEntity getSupportedLanguage() {

    logger.info("/api/v1/anki-flash-card/get-supported-language");

    initializeCardService("English");
    List<Translation> languages = cardService.getSupportedLanguages();

    return ResponseEntity.ok().body(languages);
  }

  @GetMapping(path = "/download")
  public ResponseEntity download(@RequestParam(value = "sessionId") String sessionId)
      throws IOException {

    logger.info("/api/v1/anki-flash-card/download");

    initializeCardService("English");
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

    return ResponseEntity.ok().body("Success");
  }

  private void initializeCardService(String sourceLanguage) {

    String beanPrefix = sourceLanguage.toLowerCase().split(" ")[0];
    try {
      cardService = beans.getBean(beanPrefix + "CardServiceImpl", CardService.class);
    } catch (BeansException e) {
      throw new BadRequestException(
          String.format("The language [%s] is not supported!", sourceLanguage));
    }
  }
}
