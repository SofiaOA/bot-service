package com.hedvig.botService.enteties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("audio")
public class MessageBodyAudio extends MessageBody {

	public String URL;
	private static Logger log = LoggerFactory.getLogger(MessageBodyAudio.class);
    public MessageBodyAudio(String content, String URL) {
    	super(content);
    	this.URL = URL;
	}
    MessageBodyAudio(){log.info("Instansiating MessageBodyAudio");}
}