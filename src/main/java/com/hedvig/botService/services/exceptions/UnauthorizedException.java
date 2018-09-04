package com.hedvig.botService.services.exceptions;

public class UnauthorizedException extends RuntimeException {

  private static final long serialVersionUID = -4056086095967706903L;

  public UnauthorizedException(final String message) {
    super(message);
  }
}
