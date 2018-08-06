package com.hedvig.botService.web.dto;

import java.util.List;

import lombok.Data;

@Data
public class TrackingDTO {
  String utmSource;
  String utmMedium;
  List<String> utmContent;
  String utmCampaign;
  List<String> utmTerm;
  String phoneNumber;
}
