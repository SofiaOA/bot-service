package com.hedvig.botService.enteties.message;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("number")
public class MessageBodyNumber extends MessageBody {

    public MessageBodyNumber(String content) {
    	super(content);
	}
    MessageBodyNumber(){}
}