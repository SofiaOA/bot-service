package com.hedvig.botService.enteties.message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("photo_upload")
public class MessageBodyPhotoUpload extends MessageBody {

	public String URL;
	private static Logger log = LoggerFactory.getLogger(MessageBodyPhotoUpload.class);
    public MessageBodyPhotoUpload(String content, String URL) {
    	super(content);
    	this.URL = URL;
	}
    MessageBodyPhotoUpload(){log.info("Instansiating MessageBodyPhotoUpload");}
}