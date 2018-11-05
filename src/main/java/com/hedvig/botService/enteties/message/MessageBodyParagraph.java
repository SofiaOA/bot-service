package com.hedvig.botService.enteties.message;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import lombok.ToString;

@Entity
@DiscriminatorValue("paragraph")
@ToString
public class MessageBodyParagraph extends MessageBody {

  public MessageBodyParagraph(String content) {
    super(content);
  }

  MessageBodyParagraph() {}
}
