package com.hedvig.botService.enteties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("number")
public class MessageBodyNumber extends MessageBody {

	private static Logger log = LoggerFactory.getLogger(MessageBodyNumber.class);
    public MessageBodyNumber(String content) {
    	super(content);
	}
    MessageBodyNumber(){}
}