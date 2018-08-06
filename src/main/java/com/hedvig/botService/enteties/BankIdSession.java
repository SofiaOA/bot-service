package com.hedvig.botService.enteties;

import com.hedvig.botService.serviceIntegration.memberService.dto.BankIdProgressStatus;

public interface BankIdSession {
  boolean shouldAbort();

  void setDone();

  boolean isDone();

  void addError();

  void update(BankIdProgressStatus bankIdStatus);

  Boolean getDone();

  Long getId();

  String getReferenceToken();

  String getAutoStartToken();

  CollectionType getCollectionType();

  UserContext getUserContext();

  String getLastStatus();

  Long getVersion();

  java.time.Instant getLastCallTime();

  Integer getErrorCount();

  public enum CollectionType {
    AUTH,
    SIGN
  }
}
