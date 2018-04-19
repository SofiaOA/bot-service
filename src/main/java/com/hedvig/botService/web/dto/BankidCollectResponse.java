package com.hedvig.botService.web.dto;

import lombok.Value;

@Value
public class BankidCollectResponse {
    String orderRef;
    String status;
    String hintCode;
}
