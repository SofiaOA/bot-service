package com.hedvig.botService.services.events;

import lombok.Value;

@Value
public class UnderwritingLimitExcededEvent {
  public enum UnderwritingType {
    HouseingSize,
    HouseholdSize
  }

  String memberId;
  String phoneNumber;
  String firstName;
  String lastName;
  UnderwritingType kind;
}
