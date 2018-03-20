package com.hedvig.botService.enteties.message;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("paragraph")
public class MessageBodyParagraph extends MessageBody {

    public MessageBodyParagraph(String content) {
    	super(content);
	}
    MessageBodyParagraph(){}
}