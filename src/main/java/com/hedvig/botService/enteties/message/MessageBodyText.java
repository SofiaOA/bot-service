package com.hedvig.botService.enteties.message;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import lombok.ToString;

@Entity
@DiscriminatorValue("text")
@ToString
public class MessageBodyText extends MessageBody {

  public MessageBodyText(String content) {
    super(content);
  }

  MessageBodyText() {}
}
