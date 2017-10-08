package com.hedvig.botService.chat;

import java.util.ArrayList;

import com.hedvig.botService.enteties.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SampleConversation extends Conversation {

	private static Logger log = LoggerFactory.getLogger(SampleConversation.class);	
	public SampleConversation(MemberChat mc, UserContext uc) {
		super("onboarding", mc, uc);
		init();
	}

	public void init(){
		Message m = new Message();
		m.id = "1";
		m.header = new MessageHeader(HEDVIG_USER_ID,"/response",-1); // -1 -> not sent yet
		m.body = new MessageBodyText("Hej! Vad heter du?");
		messageList.put(m.id, m);
		
		Message m2 = new Message();
		m2.id = "2";
		m2.header = new MessageHeader(HEDVIG_USER_ID,"/response",-1);
		m2.body = new MessageBodySingleSelect("Trevlig att råkas {NAME}. Här kan du välja:",
				new ArrayList<SelectItem>(){{
					add(new SelectOption("1", "blå", false));
					add(new SelectOption("2", "röd", false));
				}}
		);
		messageList.put(m2.id, m2);
		
		Message m3 = new Message();
		m3.id = "3";
		m3.header = new MessageHeader(HEDVIG_USER_ID,"/response",-1);
		m3.body = new MessageBodyText("Ok {NAME}, så du gillar {OPTION}... Jag med!");
		messageList.put(m3.id, m3);
		
		sendMessage(m); // Put first message on the outbox
	}

	@Override
	public void recieveMessage(Message m) {
		log.info(m.toString());
		switch(m.id){
		case "1": 
			String fName = m.body.text;			
			log.info("Add to context:" + "{NAME}:" + fName);
			userContext.putUserData("{NAME}", fName);
			sendMessage(messageList.get("2"));
			break;
		case "2":
			MessageBodySingleSelect body = (MessageBodySingleSelect)m.body;
			
			for(SelectItem o : body.choices){
				if(SelectLink.class.isInstance(o) && SelectLink.class.cast(o).selected){
					log.info("Add to context:" + "{OPTION}:" + SelectOption.class.cast(o).value);
					userContext.putUserData("{OPTION}", SelectOption.class.cast(o).value);
					break;
				}
			}
			sendMessage(messageList.get("3"));
			break;
		 default:
			 log.info("Unknown message recieved...");
			break;
		}

	}
}
