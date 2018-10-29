package com.hedvig.botService.enteties.message;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import lombok.ToString;

@Entity
@DiscriminatorValue("rich_text")
@ToString
public class MessageBodyRichText extends MessageBody {

  public MessageBodyRichText(String content) {
    super(content);
  }

}
