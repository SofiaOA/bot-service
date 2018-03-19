package com.hedvig.botService.enteties.message;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("text")
public class MessageBodyText extends MessageBody {

    public MessageBodyText(String content) {
    	super(content);
	}
    MessageBodyText(){}
}