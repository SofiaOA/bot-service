package com.hedvig.generic.bot.chat;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hedvig.generic.bot.session.UserContext;

public class OnboardingConversation extends Conversation {

	private static Logger log = LoggerFactory.getLogger(OnboardingConversation.class);
	
	public OnboardingConversation(ChatHistory c, UserContext u) {
		super("onboarding", c, u);
		// TODO Auto-generated constructor stub
	}

	public void init(){
		Message m = new Message();
		m.id = "message.hello";
		m.header = new MessageHeader(Conversation.HEDVIG_USER_ID,"/response",-1); // -1 -> not sent yet
		m.body = new MessageBodyMultipleChoice("Hej, det är jag som är Hedvig, din personliga försäkringsassistent! Vad kan jag hjälpa dig med?", 
				new ArrayList<Link>(){{
					add(new Link("Jag vill ha en ny", "/response", "action.new", false));
					add(new Link("Vill byta försäkring", "/response","action.change", false));
					add(new Link("Varför behöver jag?", "/response","action.why", false));
					add(new Link("Vem är du, Hedvig?", "/response","action.who", false));
				}}
		);
		messageList.put(m.id, m);
		
		Message m2 = new Message();
		m2.id = "message.getname";
		m2.header = new MessageHeader(Conversation.HEDVIG_USER_ID,"/response",-1); // -1 -> not sent yet
		m2.body = new MessageBodyText("Trevlig, vad heter du?");
		messageList.put(m2.id, m2);
		
		Message m3 = new Message();
		m3.id = "message.changecompany";
		m3.header = new MessageHeader(Conversation.HEDVIG_USER_ID,"/response",-1); // -1 -> not sent yet
		m3.body = new MessageBodyMultipleChoice("Ok, vilket bolag har du idag?", 
				new ArrayList<Link>(){{
					add(new Link("If", "/response", "company.if", false));
					add(new Link("TH", "/response","company.th", false));
					add(new Link("LF", "/response","company.lf", false));
				}}
		);
		messageList.put(m3.id, m3);
		
		Message m4 = new Message();
		m4.id = "message.whyinsurance";
		m4.header = new MessageHeader(Conversation.HEDVIG_USER_ID,"/response",-1); // -1 -> not sent yet
		m4.body = new MessageBodyText("Hemförsäkring behöver alla!");
		messageList.put(m4.id, m4);
		
		Message m5 = new Message();
		m5.id = "message.whoishedvig";
		m5.header = new MessageHeader(Conversation.HEDVIG_USER_ID,"/response",-1); // -1 -> not sent yet
		m5.body = new MessageBodyText("En försäkringsbot!");
		messageList.put(m5.id, m5);
		
		Message merror = new Message();
		merror.id = "error";
		merror.header = new MessageHeader(Conversation.HEDVIG_USER_ID,"/response",-1); // -1 -> not sent yet
		merror.body = new MessageBodyText("Oj nu blev något fel...");
		messageList.put(merror.id, merror);
		
		sendMessage(m); // Put first message on the outbox
	}

	@Override
	public void recieveMessage(Message m) {
		log.info(m.toString());
		switch(m.id){
		case "message.hello": 

			MessageBodyMultipleChoice body = (MessageBodyMultipleChoice)m.body;
			for(Link o : body.links){
				if(o.selected){
					switch(o.param){
						case "action.new": sendMessage(messageList.get("message.getname")); break;
						case "action.change": sendMessage(messageList.get("message.changecompany")); break;
						case "action.why": sendMessage(messageList.get("message.whyinsurance")); break;
						case "action.who": sendMessage(messageList.get("message.whoishedvig")); break;
					}
				}
			}
			break;

		 default:
			 log.info("Unknown message recieved...");
			 sendMessage(messageList.get("error"));
			break;
		}

	}

}
