package com.hedvig.botService.serviceIntegration.paymentService.dto;

import lombok.Value;

@Value
public class UrlResponse {
    String url;
    String orderId;
}
