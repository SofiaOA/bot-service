package com.hedvig.botService.enteties.message;

import lombok.ToString;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("number")
@ToString
public class MessageBodyNumber extends MessageBodyText {

  public MessageBodyNumber(String content) {
    super(content, KeyboardType.NUMBER_PAD);
  }

  public MessageBodyNumber(String content, String placeholder){
    this(content);
    this.placeholder = placeholder;
  }

  MessageBodyNumber() {}


}
