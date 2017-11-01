package com.hedvig.botService.enteties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("paragraph")
public class MessageBodyParagraph extends MessageBody {

	private static Logger log = LoggerFactory.getLogger(MessageBodyParagraph.class);
    public MessageBodyParagraph(String content) {
    	super(content);
	}
    MessageBodyParagraph(){}
}