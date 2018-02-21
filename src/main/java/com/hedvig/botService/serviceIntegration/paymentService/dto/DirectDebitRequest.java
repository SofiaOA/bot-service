package com.hedvig.botService.serviceIntegration.paymentService.dto;

import lombok.Value;

@Value
public class DirectDebitRequest {
    String firstName;
    String lastName;
    String ssn;
    String email;
    String memberId;
}
