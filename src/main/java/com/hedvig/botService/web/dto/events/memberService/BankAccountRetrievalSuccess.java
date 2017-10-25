package com.hedvig.botService.web.dto.events.memberService;

import lombok.EqualsAndHashCode;
import lombok.Value;
import java.util.List;

@Value
@EqualsAndHashCode(callSuper = true)
public class BankAccountRetrievalSuccess extends MemberServiceEventPayload {
    private List<BankAccountDetails> accounts;
}
