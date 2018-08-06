package com.hedvig.botService.services.events;

import lombok.Value;

@Value
public class OnboardingQuestionAskedEvent {
  private final String memberId;
  private final String question;

  public OnboardingQuestionAskedEvent(final String memberId, final String question) {

    this.memberId = memberId;
    this.question = question;
  }
}
