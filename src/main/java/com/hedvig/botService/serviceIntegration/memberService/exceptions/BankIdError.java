package com.hedvig.botService.serviceIntegration.memberService.exceptions;

public class BankIdError extends RuntimeException {

  private static final long serialVersionUID = -4423646568793198371L;
  private final ErrorType errorType;
  private final String message;

  public BankIdError(ErrorType errorType, String message) {
    this.errorType = errorType;
    this.message = message;
  }

  public ErrorType getErrorType() {
    return errorType;
  }

  public String getMessage() {
    return message;
  }
}
