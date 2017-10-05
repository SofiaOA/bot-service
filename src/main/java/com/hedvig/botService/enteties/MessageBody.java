package com.hedvig.botService.enteties;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "body_type")
@JsonTypeInfo(
	      use = JsonTypeInfo.Id.NAME, 
	      include = JsonTypeInfo.As.PROPERTY, 
	      property = "type")
	    @JsonSubTypes({
	    	@JsonSubTypes.Type(value = MessageBodyText.class, name = "text"),
	        @JsonSubTypes.Type(value = MessageBodySingleSelect.class, name = "single_select"),
	        @JsonSubTypes.Type(value = MessageBodyMultipleSelect.class, name = "multiple_select"),
	        @JsonSubTypes.Type(value = MessageBodyDatePicker.class, name = "date_picker"),
	        @JsonSubTypes.Type(value = MessageBodyAudio.class, name = "audio"),
	        @JsonSubTypes.Type(value = MessageBodyVideo.class, name = "video"),
	        @JsonSubTypes.Type(value = MessageBodyHero.class, name = "hero")
	    })
public class MessageBody {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Integer id;

	private static Logger log = LoggerFactory.getLogger(MessageBody.class);

	public String content;

	MessageBody(String content){this.content = content;}
	MessageBody(){log.info("Instansiating MessageBody");}
}
