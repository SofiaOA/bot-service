package com.hedvig.botService.serviceIntegration.paymentService.dto;

import lombok.Value;

import java.util.UUID;

@Value
public class OrderInformation {

  UUID id;

  String iframeUrl;

  OrderState state;
}
