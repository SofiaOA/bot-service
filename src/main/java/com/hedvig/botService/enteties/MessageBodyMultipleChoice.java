package com.hedvig.botService.enteties;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("multipleChoice")
public class MessageBodyMultipleChoice extends MessageBody {
	private static Logger log = LoggerFactory.getLogger(MessageBodySingleSelect.class);
	public ArrayList<Link> links = new ArrayList<>();
	
    public MessageBodyMultipleChoice(String content, ArrayList<Link> links) {
    	super(content);
		this.links.addAll(links);
	}
    MessageBodyMultipleChoice(){log.info("Instansiating MessageBodyMultipleChoice");}
}