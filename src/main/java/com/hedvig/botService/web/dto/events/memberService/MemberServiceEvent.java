package com.hedvig.botService.web.dto.events.memberService;

import lombok.Value;

import java.time.Instant;

@Value
public class MemberServiceEvent {

    private Long memberId;
    private Instant createdAt;
    private MemberServiceEventPayload payload;

}
