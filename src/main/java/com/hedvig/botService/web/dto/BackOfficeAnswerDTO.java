package com.hedvig.botService.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class BackOfficeAnswerDTO {
  private String userId;

  private String msg;
}
