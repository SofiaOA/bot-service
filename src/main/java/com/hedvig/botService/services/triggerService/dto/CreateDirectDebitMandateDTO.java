package com.hedvig.botService.services.triggerService.dto;

import lombok.Value;

@Value
public class CreateDirectDebitMandateDTO {
    String ssn;

    String firstName;
    String lastName;

    String email;

}
