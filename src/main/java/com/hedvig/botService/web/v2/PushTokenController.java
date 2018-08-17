package com.hedvig.botService.web.v2;

import com.hedvig.botService.services.SessionManager;
import com.hedvig.botService.web.v2.dto.RegisterPushTokenRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v2/push-token")
public class PushTokenController {
  private SessionManager sessionManager;

  public PushTokenController(SessionManager sessionManager) {
    this.sessionManager = sessionManager;
  }

  @PostMapping
  public ResponseEntity<Void> pushToken(@RequestBody RegisterPushTokenRequest dto,
      @RequestHeader(value = "hedvig.token") String hid) {
    sessionManager.saveFirebasePushToken(hid, dto.getFcmRegistrationToken());
    return ResponseEntity.noContent().build();
  }
}
