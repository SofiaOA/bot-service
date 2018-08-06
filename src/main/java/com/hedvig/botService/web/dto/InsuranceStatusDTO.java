package com.hedvig.botService.web.dto;

import lombok.Value;

import java.util.List;

@Value
public class InsuranceStatusDTO {

  List<String> safetyIncreasers;
  String insuranceStatus;
}
