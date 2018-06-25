package com.hedvig.botService.services.events;

import lombok.Value;

@Value
public class ClaimCallMeEvent {
    String memberId;
    String firstName;
    String familyName;
    String phoneNumber;
    boolean insuranceActive;
}
