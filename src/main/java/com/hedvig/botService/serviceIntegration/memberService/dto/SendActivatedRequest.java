package com.hedvig.botService.serviceIntegration.memberService.dto;

import lombok.Value;

@Value
public class SendActivatedRequest {
    public String name;
    public String email;
}

