package com.hedvig.botService.enteties.message;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity
@DiscriminatorValue("hero")
@ToString
public class MessageBodyHero extends MessageBody {

  public String imageUri;
  private static Logger log = LoggerFactory.getLogger(MessageBodyHero.class);

  public MessageBodyHero(String content, String URL) {
    super(content);
    this.imageUri = URL;
  }

  MessageBodyHero() {
    log.info("Instansiating MessageBodyHero");
  }
}
