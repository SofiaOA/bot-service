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

	public ArrayList<SelectOption> options;
	
    public MessageBodySingleSelect(String content, ArrayList<SelectOption> options) {
    	super(content);
		this.options = options;
	}
    MessageBodySingleSelect(){log.info("Instansiating MessageBodySingleSelect");}
}