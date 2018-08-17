package com.hedvig.botService.web.v2;

import com.hedvig.botService.services.SessionManager;
import com.hedvig.botService.web.v2.dto.PushTokenResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.val;

@RestController
@RequestMapping("/_/v2/")
public class InternalController {
  private final SessionManager sessionManager;

  public InternalController(SessionManager sessionManager) {
    this.sessionManager = sessionManager;
  }

  @GetMapping("{memberId}/push-token")
  public ResponseEntity<PushTokenResponse> pushToken(@PathVariable String memberId) {
    val token = sessionManager.getFirebasePushToken(memberId);
    if (token == null) {
      return ResponseEntity.notFound().build();
    }

    return ResponseEntity.ok(new PushTokenResponse(token));
  }
}
