package com.hedvig.botService.enteties.message;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import lombok.ToString;

@Entity
@DiscriminatorValue("number")
@ToString
public class MessageBodyNumber extends MessageBody {

  public MessageBodyNumber(String content) {
    super(content);
  }

  MessageBodyNumber() {}
}
