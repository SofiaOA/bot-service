package com.hedvig.botService.enteties.message;

import lombok.ToString;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("paragraph")
@ToString
public class MessageBodyParagraph extends MessageBody {

    public MessageBodyParagraph(String content) {
    	super(content);
	}
    MessageBodyParagraph(){}
}