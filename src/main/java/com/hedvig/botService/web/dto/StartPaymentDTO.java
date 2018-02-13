package com.hedvig.botService.web.dto;

import lombok.Value;

import java.util.UUID;

@Value
public class StartPaymentDTO {
    UUID triggerId;
    String url;
}
