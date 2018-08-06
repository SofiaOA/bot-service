package com.hedvig.botService.chat;

public class MessageNotFoundException extends RuntimeException {

  public MessageNotFoundException(String string) {
    super(string);
  }
}
