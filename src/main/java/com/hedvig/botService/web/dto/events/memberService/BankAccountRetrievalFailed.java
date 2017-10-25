package com.hedvig.botService.web.dto.events.memberService;


import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class BankAccountRetrievalFailed extends MemberServiceEventPayload {
    public String errorMsg;
}
