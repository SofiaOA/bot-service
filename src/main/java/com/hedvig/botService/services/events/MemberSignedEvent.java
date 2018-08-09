package com.hedvig.botService.services.events;

import lombok.Value;

@Value
public class MemberSignedEvent {
  String memberId;
  String productType;
}
