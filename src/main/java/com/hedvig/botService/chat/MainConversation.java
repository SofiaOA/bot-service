package com.hedvig.botService.chat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hedvig.botService.enteties.*;

public class MainConversation extends Conversation {

	private static Logger log = LoggerFactory.getLogger(MainConversation.class);
	private static DateTimeFormatter datetimeformatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
	
	public MainConversation(MemberChat mc, UserContext uc) {
		super("main.menue", mc,uc);
		// TODO Auto-generated constructor stub

		createMessage("hedvig.com",
				new MessageBodySingleSelect("Hej {NAME}, vad vill du göra idag?",
						new ArrayList<SelectItem>(){{
							add(new SelectOption("Det är kris, ring mig!","message.main.callme", false));
							add(new SelectOption("Rapportera en skada","message.main.report", false));
							add(new SelectOption("Rekommendera en vän","message.main.refer", false));
							add(new SelectOption("Ställ en fråga","main.question", false));
						}}
				));
		
		createMessage("message.main.callme", new MessageBodyText("Ok, ta det lugnt jag ringer!"));
		
		createMessage("error", new MessageBodyText("Oj nu blev något fel..."));
	}

	@Override
	public void recieveMessage(Message m) {
		log.info(m.toString());
		
		switch(m.id){
		case "message.getname": 

			String fName = m.body.text;			
			log.info("Add to context:" + "{NAME}:" + fName);
			userContext.putUserData("{NAME}", fName);
			m.body.text = "Jag heter " + fName;
			addToChat(m); // Response parsed to nice format
			addToChat(getMessage("message.greetings"));
			
			break;

		case "message.greetings": 

			LocalDateTime bDate = ((MessageBodyDatePicker)m.body).date;			
			log.info("Add to context:" + "{BIRTH_DATE}:" + bDate.toString());
			userContext.putUserData("{BIRTH_DATE}", bDate.toString());
			addToChat(getMessage("message.bye"));
			
			break;
			
		 default:
			 /*
			  * In a Single select, there is only one trigger event. Set default here to be a link to a new message
			  */
			if(m.body.getClass().equals(MessageBodySingleSelect.class) ){
				
				MessageBodySingleSelect body = (MessageBodySingleSelect)m.body;
				for(SelectItem o : body.choices){
					if(SelectOption.class.isInstance(o) && SelectOption.class.cast(o).selected){
						m.body.text = SelectOption.class.cast(o).text;
						addToChat(m);
						addToChat(getMessage(SelectOption.class.cast(o).value));
					}
				}
			}
			else{
				log.info("Unknown message recieved...");
				addToChat(getMessage("error"));
			}
			 
			break;
		}
		
	}

	@Override
	public void init() {
    	log.info("Starting main conversation");
        startConversation("hedvig.com"); // Id of first message
		
	}

	@Override
	public void recieveEvent(EventTypes e, String value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void completeRequest(String nxtMsg) {
		// TODO Auto-generated method stub
		
	}

}
