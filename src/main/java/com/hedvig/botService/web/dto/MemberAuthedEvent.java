package com.hedvig.botService.web.dto;

import java.time.Instant;
import lombok.Value;

@Value
public class MemberAuthedEvent {

  private String eventId;
  private Long memberId;
  private Instant createdAt;
  private Member member;
}
