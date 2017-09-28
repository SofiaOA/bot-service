package com.hedvig.generic.bot.chat;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hedvig.generic.bot.session.UserContext;

public class OnboardingConversation extends Conversation {

	private static Logger log = LoggerFactory.getLogger(OnboardingConversation.class);
	
	public OnboardingConversation(UserContext u) {
		super("onboarding", u);
		// TODO Auto-generated constructor stub
	}

	public void init(){
		
		createMessage("message.hello",
		new MessageBodyMultipleChoice("Hej, det är jag som är Hedvig, din personliga försäkringsassistent! Vad kan jag hjälpa dig med?", 
				new ArrayList<Link>(){{
					add(new Link("Jag vill ha en ny", "/response", "message.getname", false));
					add(new Link("Vill byta försäkring", "/response","message.changecompany", false));
					add(new Link("Varför behöver jag?", "/response","message.whyinsurance", false));
					add(new Link("Vem är du, Hedvig?", "/response","message.whoishedvig", false));
				}}
		));

		createMessage("message.getname", new MessageBodyText("Trevlig, vad heter du?"));
		
		createMessage("message.changecompany",
		new MessageBodyMultipleChoice("Ok, vilket bolag har du idag?", 
				new ArrayList<Link>(){{
					add(new Link("If", "/response", "company.if", false));
					add(new Link("TH", "/response","company.th", false));
					add(new Link("LF", "/response","company.lf", false));
				}}
		));

		createMessage("message.whyinsurance", new MessageBodyText("Hemförsäkring behöver alla!"));
		createMessage("message.whoishedvig", new MessageBodyText("En försäkringsbot!"));
		createMessage("error", new MessageBodyText("Oj nu blev något fel..."));
		
		startConversation("message.hello"); // Id of first message
	}

	@Override
	public void recieveMessage(Message m) {
		log.info(m.toString());
		
		// Follow the selected link
		if(m.body.getClass().equals(MessageBodyMultipleChoice.class)){
			
			MessageBodyMultipleChoice body = (MessageBodyMultipleChoice)m.body;
			for(Link o : body.links){
				if(o.selected){
					sendMessage(messageList.get(o.param));
				}
			}
		}
		
		/*switch(m.id){
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
		}*/

	}

}
