package com.hedvig.botService.web.dto;

import lombok.Value;

@Value
public class BankIdCollectError {
    String errorCode;
    String message;
}