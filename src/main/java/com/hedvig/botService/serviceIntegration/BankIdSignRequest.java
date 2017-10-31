package com.hedvig.botService.serviceIntegration;

import lombok.Value;

@Value
public class BankIdSignRequest {

    private String ssn;
    private String userMessage;

}
