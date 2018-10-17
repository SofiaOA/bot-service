package com.hedvig.botService.enteties.message;


import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity
@DiscriminatorValue("file")
@ToString
public class MessageBodyFileUpload extends MessageBody {

  private static Logger logger = LoggerFactory.getLogger(MessageBodyAudio.class);

  public String key;
  public String type;

  public MessageBodyFileUpload(String content, String key, String type) {
    super(content);
    logger.debug("A FileUpload MessageBody was created with content {} and key {}, and type: {}", content, key, type);
    this.key = key;
    this.type = type;
  }
}
