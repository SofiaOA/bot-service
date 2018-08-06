package com.hedvig.botService.enteties.message;

import lombok.ToString;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("text")
@ToString
public class MessageBodyText extends MessageBody {

  public MessageBodyText(String content) {
    super(content);
  }

  MessageBodyText() {}
}
