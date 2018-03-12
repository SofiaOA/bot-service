package com.hedvig.botService.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class BackOfficeAnswerDTO {

    public String userId;

    public String msg;
}
