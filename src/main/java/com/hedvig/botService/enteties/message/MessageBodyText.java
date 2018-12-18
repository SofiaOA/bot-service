package com.hedvig.botService.enteties.message;

import lombok.ToString;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

@Entity
@DiscriminatorValue("text")
@ToString
public class MessageBodyText extends MessageBody {



  public MessageBodyText(String content) {
    super(content);
  }

  public MessageBodyText(String content, KeyboardType keyboardType) {
    super(content);
    this.keyboardType = keyboardType;
  }

  public MessageBodyText(String content, KeyboardType keyboardType, String placeholder) {
    this(content, keyboardType);
    this.placeholder = placeholder;
  }

  MessageBodyText() {}

  public KeyboardType keyboardType = KeyboardType.DEFAULT;

  @Transient
  public String placeholder;
}
