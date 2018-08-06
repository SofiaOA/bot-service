package com.hedvig.botService.web.dto;

import lombok.Value;

@Value
public class AddMessageRequestDTO {
  String memberId;
  String msg;
}
