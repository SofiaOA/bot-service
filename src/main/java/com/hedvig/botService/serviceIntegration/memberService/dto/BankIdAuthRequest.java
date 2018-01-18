package com.hedvig.botService.serviceIntegration.memberService.dto;

import lombok.Value;

@Value
public class BankIdAuthRequest {
    private String ssn;
    private String memberId;
}
