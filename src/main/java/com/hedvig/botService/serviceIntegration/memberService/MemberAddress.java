package com.hedvig.botService.serviceIntegration.memberService;

import lombok.Value;

@Value
public class MemberAddress {
    private final String street;
    private final String city;
    private final String zipCode;
}
