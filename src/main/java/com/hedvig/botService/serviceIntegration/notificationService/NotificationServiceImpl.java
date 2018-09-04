package com.hedvig.botService.serviceIntegration.notificationService;

import feign.FeignException;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class NotificationServiceImpl implements NotificationService {

  private final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);
  private final NotificationServiceClient notificationServiceClient;

  public NotificationServiceImpl(NotificationServiceClient notificationServiceClient) {
    this.notificationServiceClient = notificationServiceClient;
  }

  @Override
  public Optional<String> getFirebaseToken(String memberId) {
    try {
      return Optional.of(notificationServiceClient.getFirebaseToken(memberId).getBody());
    } catch (FeignException e) {
      if (e.status() == 404) {
        return Optional.empty();
      } else {
        logger.error("Something wrong with the notification-service, {}", e);
        throw e;
      }
    }
  }

  @Override
  public void setFirebaseToken(String memberId, String token) {
    notificationServiceClient.setFirebaseToken(memberId, token);
  }
}
