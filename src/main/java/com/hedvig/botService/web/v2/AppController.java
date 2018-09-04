package com.hedvig.botService.web.v2;

import com.hedvig.botService.services.MessagesService;
import com.hedvig.botService.services.SessionManager;
import com.hedvig.botService.web.v2.dto.FABAction;
import com.hedvig.botService.web.v2.dto.MessagesDTO;
import java.util.Objects;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/v2/app")
public class AppController {

  private final MessagesService messagesService;

  public AppController(MessagesService messagesService) {
    this.messagesService = messagesService;
  }

  @GetMapping("/")
  public MessagesDTO getMessages(
      @RequestHeader("hedvig.token") String hid,
      @RequestParam(name = "intent", required = false, defaultValue = "onboarding")
          String intentParameter) {

    SessionManager.Intent intent =
        Objects.equals(intentParameter, "login")
            ? SessionManager.Intent.LOGIN
            : SessionManager.Intent.ONBOARDING;
    return this.messagesService.getMessagesAndStatus(hid, intent);
  }

  @PostMapping("fabTrigger/{actionId}")
  public ResponseEntity<?> fabTrigger(
      @RequestHeader("hedvig.token") String hid, @PathVariable FABAction actionId) {

    return this.messagesService.fabTrigger(hid, actionId);
  }
}
