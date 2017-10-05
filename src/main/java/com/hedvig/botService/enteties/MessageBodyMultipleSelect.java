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
	
	public ArrayList<SelectItem> items = new ArrayList<>();
	
    public MessageBodyMultipleSelect(String content, ArrayList<SelectItem> links) {
    	super(content);
		this.items.addAll(links);
	}
    MessageBodyMultipleSelect(){log.info("Instansiating MessageBodyMultipleChoice");}
}