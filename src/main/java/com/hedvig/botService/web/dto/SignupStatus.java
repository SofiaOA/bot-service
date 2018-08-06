package com.hedvig.botService.web.dto;

public class SignupStatus {

  public static enum states {
    WAITLIST,
    NOT_FOUND,
    ACCESS,
    USED
  }

  public Integer position = -1;
  public String status;
  public String code;

  public SignupStatus() {}

  public SignupStatus(Integer position, String status, String code) {

    this.position = position;
    this.status = status;
    this.code = code;
  }
}
