package com.hedvig.botService.web.v2;

import com.hedvig.botService.serviceIntegration.notificationService.NotificationService;
import java.util.Optional;
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
  public ResponseEntity<String> pushToken(@PathVariable String memberId) {

    Optional<String> possibleToken = notificationService.getFirebaseToken(memberId);

    return possibleToken.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
  }
}
