package com.hedvig.botService.enteties;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("multipleChoice")
public class MessageBodyMultipleSelect extends MessageBody {
	private static Logger log = LoggerFactory.getLogger(MessageBodySingleSelect.class);
	
	public ArrayList<SelectItem> choices = new ArrayList<SelectItem>();
	
    public MessageBodyMultipleSelect(String content, ArrayList<SelectItem> items) {
    	super(content);
		this.choices.addAll(items); // TODO
	}
    MessageBodyMultipleSelect(){log.info("Instansiating MessageBodyMultipleChoice");}
}