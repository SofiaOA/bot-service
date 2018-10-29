package com.hedvig.botService.services.events;

import lombok.Value;

@Value
public class OnboardingFileUploadedEvent {
  String memberId;
  String key;
  String mimeType;
}
