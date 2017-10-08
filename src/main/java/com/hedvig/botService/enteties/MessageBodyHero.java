package com.hedvig.botService.enteties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("hero")
public class MessageBodyHero extends MessageBody {

	public String URL;
	private static Logger log = LoggerFactory.getLogger(MessageBodyHero.class);
    public MessageBodyHero(String content, String URL) {
    	super(content);
    	this.URL = URL;
	}
    MessageBodyHero(){log.info("Instansiating MessageBodyHero");}
}