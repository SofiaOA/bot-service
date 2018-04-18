package com.hedvig.botService.enteties.message;

import lombok.ToString;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("number")
@ToString
public class MessageBodyNumber extends MessageBody {

    public MessageBodyNumber(String content) {
    	super(content);
	}
    MessageBodyNumber(){}
}