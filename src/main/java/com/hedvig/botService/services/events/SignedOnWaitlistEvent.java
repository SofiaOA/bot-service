package com.hedvig.botService.services.events;

import lombok.Value;

@Value
public class SignedOnWaitlistEvent {
  String email;
}
