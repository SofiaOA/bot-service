package com.hedvig.botService.session.events;

import lombok.Value;

@Value
public class SignedOnWaitlistEvent {
    String email;
}
