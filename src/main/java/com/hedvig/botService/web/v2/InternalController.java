package com.hedvig.botService.web.v2;

import com.hedvig.botService.serviceIntegration.notificationService.NotificationService;
import com.hedvig.botService.web.v2.dto.PushTokenResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/_/v2/")
public class InternalController {

  private NotificationService notificationService;

  public InternalController(NotificationService notificationService) {
    this.notificationService = notificationService;
  }

  @GetMapping("{memberId}/push-token")
  public ResponseEntity<PushTokenResponse> pushToken(@PathVariable String memberId) {
    String token = notificationService.getFirebaseToken(memberId);

    if (token == null) {
      return ResponseEntity.notFound().build();
    }

    return ResponseEntity.ok(new PushTokenResponse(token));
  }
}
