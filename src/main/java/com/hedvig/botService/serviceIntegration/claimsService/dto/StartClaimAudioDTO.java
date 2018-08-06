package com.hedvig.botService.serviceIntegration.claimsService.dto;

import lombok.Value;

import java.time.LocalDateTime;

@Value
public class StartClaimAudioDTO {

  String userId;
  LocalDateTime registrationDate;
  String audioURL;
}
