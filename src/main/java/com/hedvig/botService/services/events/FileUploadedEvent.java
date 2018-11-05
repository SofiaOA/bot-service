package com.hedvig.botService.services.events;

import lombok.Value;

@Value
public class FileUploadedEvent {
  String memberId;
  String key;
  String mimeType;
}
