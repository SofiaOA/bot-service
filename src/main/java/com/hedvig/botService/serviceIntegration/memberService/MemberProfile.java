package com.hedvig.botService.serviceIntegration.memberService;

import java.time.LocalDate;
import java.util.Optional;
import lombok.Value;

@Value
public class MemberProfile {

  private final String memberId;
  private final String ssn;

  private final String firstName;
  private final String lastName;

  private final Optional<MemberAddress> address;

  private final String email;
  private final String phoneNumber;
  private final String country;

  private final LocalDate birthDate;
}
