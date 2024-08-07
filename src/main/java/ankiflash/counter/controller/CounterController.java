package ankiflash.counter.controller;

import ankiflash.counter.payload.CounterResponse;
import ankiflash.counter.service.CounterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/anonymous")
class CounterController {

  private static final Logger logger = LoggerFactory.getLogger(CounterController.class);

  @Autowired private CounterService counterService;

  @GetMapping("/get-counter")
  public ResponseEntity getCounter() {

    logger.info("/get-counter");

    CounterResponse counter = counterService.getCounter();
    return ResponseEntity.ok().body(counter);
  }

  @GetMapping("/add-visit")
  public ResponseEntity addVisit() {

    logger.info("/add-visit");

    counterService.addVisit();
    return ResponseEntity.ok().build();
  }
}
