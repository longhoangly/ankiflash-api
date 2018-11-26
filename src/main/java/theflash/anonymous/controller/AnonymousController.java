package theflash.anonymous.controller;

import java.util.HashMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/anonymous")
public class AnonymousController {

  @GetMapping("/index")
  public HashMap<String, Object> index() {
    HashMap<String, Object> map = new HashMap<>();
    map.put("message", "Authenticated and authorised successfully !");
    map.put("result", "success!");
    return map;
  }
}
