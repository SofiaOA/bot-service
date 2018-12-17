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

  MessageBodyText() {}

  public KeyboardType keyboardType = KeyboardType.DEFAULT;

  @Transient
  String placeholder;
}
