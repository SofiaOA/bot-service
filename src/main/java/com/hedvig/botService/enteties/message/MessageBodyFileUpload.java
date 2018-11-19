package com.hedvig.botService.enteties.message;


import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity
@DiscriminatorValue("file_upload")
@ToString
@NoArgsConstructor
public class MessageBodyFileUpload extends MessageBody {

  private static Logger logger = LoggerFactory.getLogger(MessageBodyFileUpload.class);

  public String key;
  public String mimeType;

  public MessageBodyFileUpload(String content, String key, String mimeType) {
    super(content);
    logger.debug("A FileUpload MessageBody was created with content {}, key {}, mimeType {}", content, key, mimeType);
    this.key = key;
    this.mimeType = mimeType;
  }
}
