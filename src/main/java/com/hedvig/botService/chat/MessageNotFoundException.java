package com.hedvig.botService.chat;

public class MessageNotFoundException extends RuntimeException {

  private static final long serialVersionUID = -181228100025018624L;

  public MessageNotFoundException(String string) {
    super(string);
  }
}
