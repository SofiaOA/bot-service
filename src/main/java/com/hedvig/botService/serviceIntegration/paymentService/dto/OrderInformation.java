package com.hedvig.botService.serviceIntegration.paymentService.dto;

import java.util.UUID;
import lombok.Value;

@Value
public class OrderInformation {

  UUID id;

  String iframeUrl;

  OrderState state;
}
