package com.hedvig.botService.enteties;

import java.time.LocalDateTime;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity
@DiscriminatorValue("datePicker")
public class MessageBodyDatePicker extends MessageBody {
	private static Logger log = LoggerFactory.getLogger(MessageBodyDatePicker.class);

	public LocalDateTime date;
	
    public MessageBodyDatePicker(String content, LocalDateTime date) {
    	super(content);
		this.date = date;
	}
    MessageBodyDatePicker(){log.debug("Instansiating MessageBodyDatePicker");}
}