package com.hedvig.botService.serviceIntegration.notificationService;

import java.util.Optional;

public interface NotificationService {

  Optional<String> getFirebaseToken(String memberId);

  void setFirebaseToken(String memberId, String token);
}
