package com.hedvig.botService.enteties.message;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity
@DiscriminatorValue("audio")
@ToString
public class MessageBodyAudio extends MessageBody {

  public String url;
  private static Logger log = LoggerFactory.getLogger(MessageBodyAudio.class);

  public MessageBodyAudio(String content, String url) {
    super(content);
    this.url = url;
  }

  MessageBodyAudio() {
    log.info("Instansiating MessageBodyAudio");
  }
}
