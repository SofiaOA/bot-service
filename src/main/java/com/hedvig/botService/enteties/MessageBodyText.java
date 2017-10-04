package com.hedvig.botService.enteties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("text")
public class MessageBodyText extends MessageBody {

	private static Logger log = LoggerFactory.getLogger(MessageBodyText.class);
    public MessageBodyText(String content) {
    	super(content);
	}
    MessageBodyText(){log.info("Instansiating MessageBodyText");}
}