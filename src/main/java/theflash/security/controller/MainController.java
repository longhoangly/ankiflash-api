package theflash.security.controller;

import java.util.HashMap;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/index")
public class MainController {

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public HashMap<String, Object> index() {

    HashMap<String, Object> map = new HashMap<>();
    map.put("message", "Authenticated and authorised successfully !");
    map.put("result", "success!");

    return map;
  }

}
