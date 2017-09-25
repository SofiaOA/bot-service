package com.hedvig.generic.bot.chat;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageBodyMultipleChoice extends MessageBody {
	private static Logger log = LoggerFactory.getLogger(MessageBodySingleSelect.class);
	public ArrayList<Link> links;
	
    public MessageBodyMultipleChoice(String content, ArrayList<Link> links) {
    	super(content);
		this.links = links;
	}
    MessageBodyMultipleChoice(){log.info("Instansiating MessageBodyMultipleChoice");}
}