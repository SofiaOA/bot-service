package com.hedvig.botService.web.dto;

import java.util.UUID;
import lombok.Value;

@Value
public class StartPaymentDTO {
  UUID triggerId;
  String url;
}
