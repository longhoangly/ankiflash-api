package ankiflash.card.controller;

import ankiflash.card.dto.Card;
import ankiflash.card.payload.CardRequest;
import ankiflash.card.payload.WordResponse;
import ankiflash.card.service.CardGeneratingService;
import ankiflash.card.service.CardStorageService;
import ankiflash.card.utility.Constant;
import ankiflash.card.utility.Status;
import ankiflash.card.utility.Translation;
import ankiflash.security.service.UserService;
import ankiflash.utility.AnkiFlashProps;
import ankiflash.utility.IOUtility;
import ankiflash.utility.exception.BadRequestException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/anki-flash-card")
class CardController {

  private static final Logger logger = LoggerFactory.getLogger(CardController.class);

  @Autowired private UserService userService;

  @Autowired private CardStorageService cardStorageService;

  @Autowired private BeanFactory beans;

  private CardGeneratingService cardGeneratingService;

  @PostMapping("/get-words")
  public ResponseEntity getWords(@RequestBody @Valid CardRequest reqCard) {

    logger.info("/api/v1/anki-flash-card/get-words");

    String username = userService.getCurrentUsername();
    if (username.isEmpty()) {
      throw new AuthorizationServiceException("Unauthorized user!");
    }

    initializeCardService(reqCard.getSource());
    String[] words = reqCard.getWords().split(Constant.MAIN_DELIMITER);
    Translation translation = new Translation(reqCard.getSource(), reqCard.getTarget());

    List<String> success = new ArrayList<>();
    List<String> failure = new ArrayList<>();
    for (String word : words) {
      List<String> matchedWords = cardGeneratingService.getWords(word, translation);
      if (matchedWords.isEmpty()) {
        failure.add(word);
      } else {
        success.addAll(matchedWords);
      }
    }

    WordResponse respWords = new WordResponse();
    respWords.setSuccess(success);
    respWords.setFailure(failure);

    return ResponseEntity.ok().body(respWords);
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
    Card card = cardGeneratingService.generateCard(word, translation, "", reqCard.getIsOffline());
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
    List<String> words = Arrays.asList(reqCard.getWords().split(Constant.MAIN_DELIMITER));
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
        cardGeneratingService.generateCards(words, translation, ankiDir, reqCard.getIsOffline());
    for (Card card : cards) {
      if (card.getStatus().compareTo(Status.Success) == 0) {
        String soundHtml = reqCard.getIsOffline() ? card.getSoundOffline() : card.getSoundOnline();
        String imageHtml = reqCard.getIsOffline() ? card.getImageOffline() : card.getImageOnline();
        String cardContent =
            card.getWord()
                + Constant.TAB
                + card.getWordType()
                + Constant.TAB
                + card.getPhonetic()
                + Constant.TAB
                + card.getExample()
                + Constant.TAB
                + soundHtml
                + Constant.TAB
                + imageHtml
                + Constant.TAB
                + card.getMeaning()
                + Constant.TAB
                + card.getCopyright()
                + Constant.TAB
                + card.getTag()
                + "\n";
        IOUtility.write(ankiDir + "/" + Constant.ANKI_DECK, cardContent);
      } else {
        IOUtility.write(
            ankiDir + "/" + Constant.ANKI_FAILURE,
            card.getWord()
                + " => "
                + card.getStatus()
                + Constant.SUB_DELIMITER
                + card.getComment()
                + "\n");
      }
    }

    return ResponseEntity.ok().body(cards);
  }

  @GetMapping(path = "/get-supported-language")
  public ResponseEntity getSupportedLanguage() {

    logger.info("/api/v1/anki-flash-card/get-supported-language");

    initializeCardService("English");
    List<Translation> languages = cardGeneratingService.getSupportedLanguages();

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

    String zipFilePath = ankiDir + ".zip";
    File zipFile = new File(zipFilePath);
    if (!zipFile.exists()) {
      cardGeneratingService.compressResources(ankiDir);
    }

    InputStreamResource resource =
        new InputStreamResource(new FileInputStream(zipFile.getAbsolutePath()));
    return ResponseEntity.ok()
        .contentLength(zipFile.length())
        .contentType(MediaType.APPLICATION_OCTET_STREAM)
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
      cardGeneratingService =
          beans.getBean(beanPrefix + "CardGeneratingServiceImpl", CardGeneratingService.class);
    } catch (BeansException e) {
      throw new BadRequestException(
          String.format("The language [%s] is not supported!", sourceLanguage));
    }
  }
}
