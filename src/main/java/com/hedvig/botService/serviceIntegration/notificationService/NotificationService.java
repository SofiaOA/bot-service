package com.hedvig.botService.serviceIntegration.notificationService;

public interface NotificationService {

  String getFirebaseToken(String memberId);

  void setFirebaseToken(String memberId, String token);
}
