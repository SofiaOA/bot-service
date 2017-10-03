package com.hedvig.botService.chat;

import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@JsonTypeInfo(
	      use = JsonTypeInfo.Id.NAME, 
	      include = JsonTypeInfo.As.PROPERTY, 
	      property = "type")
	    @JsonSubTypes({
	    	@JsonSubTypes.Type(value = MessageBodyText.class, name = "text"),
	        @JsonSubTypes.Type(value = MessageBodySingleSelect.class, name = "single_select"),
	        @JsonSubTypes.Type(value = MessageBodyMultipleChoice.class, name = "multiple_choice")
	    })
public class MessageBody {

	private static Logger log = LoggerFactory.getLogger(MessageBody.class);
	public String content;

	MessageBody(String content){this.content = content;}
	MessageBody(){log.info("Instansiating MessageBody");}
}
