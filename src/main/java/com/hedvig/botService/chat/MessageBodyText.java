package com.hedvig.botService.chat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageBodyText extends MessageBody {

	private static Logger log = LoggerFactory.getLogger(MessageBodyText.class);
    public MessageBodyText(String content) {
    	super(content);
	}
    MessageBodyText(){log.info("Instansiating MessageBodyText");}
}