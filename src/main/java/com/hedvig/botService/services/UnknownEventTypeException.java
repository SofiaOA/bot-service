package com.hedvig.botService.services;

public class UnknownEventTypeException extends RuntimeException {

  private static final long serialVersionUID = -4297803663085177499L;

  public UnknownEventTypeException(String string) {
    super(string);
  }
}
