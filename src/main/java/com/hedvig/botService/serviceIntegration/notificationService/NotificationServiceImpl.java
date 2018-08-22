package com.hedvig.botService.serviceIntegration.notificationService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class NotificationServiceImpl implements NotificationService {

  private final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);
  private final NotificationServiceClient notificationServiceClient;

  public NotificationServiceImpl(NotificationServiceClient notificationServiceClient) {
    this.notificationServiceClient = notificationServiceClient;
  }

  @Override
  public String getFirebaseToken(String memberId) {
    ResponseEntity<String> response = notificationServiceClient.getFirebaseToken(memberId);
    return response.getBody();
  }

  @Override
  public void setFirebaseToken(String memberId, String token) {
    notificationServiceClient.setFirebaseToken(memberId, token);
  }
}
