package com.hedvig.botService.serviceIntegration.paymentService.dto;

import lombok.Value;

@Value
public class DirectDebitResponse {
  String url;
  String orderId;
}
