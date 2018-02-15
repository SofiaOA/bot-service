package com.hedvig.botService.session.events;

import lombok.Value;

@Value
public class RequestPhoneCallEvent {

    String memberId;

    String phoneNumber;

    String firstName;
    String lastName;


}
