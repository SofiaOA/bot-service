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
		super("onboarding", mc,uc);
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

	public void init(String hid){
		startConversation("hedvig.main"); // Id of first message
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
			putMessage(m); // Response parsed to nice format
			putMessage(messageList.get("message.greetings"));
			
			break;

		case "message.greetings": 

			LocalDateTime bDate = ((MessageBodyDatePicker)m.body).date;			
			log.info("Add to context:" + "{BIRTH_DATE}:" + bDate.toString());
			userContext.putUserData("{BIRTH_DATE}", bDate.toString());
			putMessage(messageList.get("message.bye"));
			
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
						putMessage(m);
						putMessage(messageList.get(SelectOption.class.cast(o).value));
					}
				}
			}
			else{
				log.info("Unknown message recieved...");
				putMessage(messageList.get("error"));
			}
			 
			break;
		}
		
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

}