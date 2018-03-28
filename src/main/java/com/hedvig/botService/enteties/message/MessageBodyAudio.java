package com.hedvig.botService.enteties.message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("audio")
public class MessageBodyAudio extends MessageBody {

	public String url;
	private static Logger log = LoggerFactory.getLogger(MessageBodyAudio.class);
    public MessageBodyAudio(String content, String url) {
    	super(content);
    	this.url = url;
	}
    MessageBodyAudio(){log.info("Instansiating MessageBodyAudio");}
}