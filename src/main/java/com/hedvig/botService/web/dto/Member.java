package com.hedvig.botService.web.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class Member {

  private final Long memberId;
  private final String ssn;

  private final String firstName;
  private final String lastName;

  private final String street;
  private final String city;
  private final String zipCode;
  private final Integer floor;

  private final String email;
  private final String phoneNumber;
  private final String country;

  private final LocalDate birthDate;
  private final String apartment;
}
