package com.hedvig.botService.chat;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageBodySingleSelect extends MessageBody {
	private static Logger log = LoggerFactory.getLogger(MessageBodySingleSelect.class);
	public ArrayList<SelectOption> options;
	
    public MessageBodySingleSelect(String content, ArrayList<SelectOption> options) {
    	super(content);
		this.options = options;
	}
    MessageBodySingleSelect(){log.info("Instansiating MessageBodySingleSelect");}
}