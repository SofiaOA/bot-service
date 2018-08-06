package com.hedvig.botService.services.exceptions;

public class UnathorizedException extends RuntimeException {

  public UnathorizedException(final String message) {
    super(message);
  }
}
