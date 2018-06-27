package com.hedvig.botService.services.events;

import lombok.Value;

@Value
public class QuestionAskedEvent {
    String memberId;
    String question;
}
