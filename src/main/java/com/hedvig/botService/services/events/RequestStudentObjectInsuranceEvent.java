package com.hedvig.botService.services.events;

import lombok.Value;

@Value
public class RequestStudentObjectInsuranceEvent {
  String memberId;
  String productType;
}
