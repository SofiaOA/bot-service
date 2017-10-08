package com.hedvig.botService.enteties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.util.ArrayList;

@Entity
@DiscriminatorValue("singleSelect")
public class MessageBodySingleSelect extends MessageBody {
	private static Logger log = LoggerFactory.getLogger(MessageBodySingleSelect.class);

	public ArrayList<SelectItem> choices = new ArrayList<SelectItem>();
	
    public MessageBodySingleSelect(String content, ArrayList<SelectItem> items) {
    	super(content);
    	this.choices.addAll(items);
	}
    MessageBodySingleSelect(){log.info("Instansiating MessageBodySingleSelect");}
}