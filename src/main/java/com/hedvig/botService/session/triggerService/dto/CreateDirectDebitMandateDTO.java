package com.hedvig.botService.session.triggerService.dto;

import lombok.Value;

@Value
public class CreateDirectDebitMandateDTO {
    String ssn;

    String firstName;
    String lastName;

    String email;

}
