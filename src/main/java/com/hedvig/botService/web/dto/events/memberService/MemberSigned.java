package com.hedvig.botService.web.dto.events.memberService;

import lombok.Value;

@Value
public class MemberSigned extends MemberServiceEventPayload {

    private final String referenceId;

}
