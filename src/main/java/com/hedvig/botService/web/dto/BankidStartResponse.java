package com.hedvig.botService.web.dto;

import lombok.Value;

@Value
public class BankidStartResponse {
  String autoStartToken;
  String orderRef;
}
