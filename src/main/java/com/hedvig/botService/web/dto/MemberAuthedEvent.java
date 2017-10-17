package com.hedvig.botService.web.dto;

import lombok.Value;

import java.time.Instant;

@Value
public class MemberAuthedEvent {

    private String eventId;
    private Long memberId;
    private Instant createdAt;
    private Profile profile;
}
