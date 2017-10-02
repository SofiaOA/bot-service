package com.hedvig.generic.bot.chat;

import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.hedvig.generic.bot.session.UserContext;

public class SampleConversation extends Conversation {

	private static Logger log = LoggerFactory.getLogger(SampleConversation.class);	
	public SampleConversation(UserContext u) {
		super("onboarding", u);
		init();
	}

	public void init(){
		Message m = new Message();
		m.id = "1";
		m.header = new MessageHeader(Conversation.HEDVIG_USER_ID,"/response",-1); // -1 -> not sent yet
		m.body = new MessageBodyText("Hej! Vad heter du?");
		messageList.put(m.id, m);
		
		Message m2 = new Message();
		m2.id = "2";
		m2.header = new MessageHeader(Conversation.HEDVIG_USER_ID,"/response",-1);
		m2.body = new MessageBodySingleSelect("Trevlig att råkas {NAME}. Här kan du välja:", 
				new ArrayList<SelectOption>(){{
					add(new SelectOption(1, "blå", false));
					add(new SelectOption(2, "röd", false));
				}}
		);
		messageList.put(m2.id, m2);
		
		Message m3 = new Message();
		m3.id = "3";
		m3.header = new MessageHeader(Conversation.HEDVIG_USER_ID,"/response",-1);
		m3.body = new MessageBodyText("Ok {NAME}, så du gillar {OPTION}... Jag med!");
		messageList.put(m3.id, m3);
		
		sendMessage(m); // Put first message on the outbox
	}

	@Override
	public void recieveMessage(Message m) {
		log.info(m.toString());
		switch(m.id){
		case "1": 
			String fName = m.body.content;
			userContext.userFirstName = fName;
			
			log.info("Add to context:" + "{NAME}:" + fName);
			conversationContext.put("{NAME}", fName);
			sendMessage(messageList.get("2"));
			break;
		case "2":
			MessageBodySingleSelect body = (MessageBodySingleSelect)m.body;
			
			for(SelectOption o : body.options){
				if(o.selected){
					log.info("Add to context:" + "{NAME}:" + o.value);
					conversationContext.put("{OPTION}", o.value);
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