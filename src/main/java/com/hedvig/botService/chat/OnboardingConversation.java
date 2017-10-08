package com.hedvig.botService.chat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hedvig.botService.enteties.*;

public class OnboardingConversation extends Conversation {

	private static Logger log = LoggerFactory.getLogger(OnboardingConversation.class);
	private static DateTimeFormatter datetimeformatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
	
	public OnboardingConversation(MemberChat mc, UserContext uc) {
		super("onboarding", mc,uc);
		// TODO Auto-generated constructor stub

		createMessage("message.hello",
				new MessageBodySingleSelect("Hej, det är jag som är Hedvig, din personliga försäkringsassistent! Vad kan jag hjälpa dig med?",
						new ArrayList<SelectItem>(){{
							add(new SelectOption("Jag vill ha en ny","message.getname", false));
							add(new SelectOption("Vill byta försäkring","message.changecompany", false));
							add(new SelectOption("Varför behöver jag?","message.whyinsurance", false));
							add(new SelectOption("Vem är du, Hedvig?","message.whoishedvig", false));
						}}
				));

		createMessage("message.getname", new MessageBodyText("Trevlig, vad heter du?"));
		
		createMessage("message.greetings", new MessageBodyDatePicker("Hej {NAME}, kul att du gillar försäkring :). När är du född?",LocalDateTime.parse("1986-04-08 00:00", datetimeformatter)));

		createMessage("message.bye", new MessageBodySingleSelect("Ok {NAME}, så det jag vet om dig är att du är förr {BIRTH_DATE}, jag hör av mig!",
					new ArrayList<SelectItem>(){{
						add(new SelectOption("Starta bank id", "message.bankid.status", false));
						add(new SelectOption("Spela in audio", "message.audio", false));
						add(new SelectOption("Spela in video", "message.video", false));
						add(new SelectOption("You need a hero!", "message.hero", false));
					}}				
				));
		
		createMessage("message.audio", new MessageBodyAudio("Här kan du spela in en berättelse om vad som hänt...", "http://audiouploadurl"));
		
		createMessage("message.video", new MessageBodyAudio("Här kan du spela in en video om vad som hänt...", "http://videoploadurl"));
		
		createMessage("message.hero", new MessageBodyHero("You need a hero!", "http://www.comedyflavors.com/wp-content/uploads/2015/02/hero.gif"));
		
		
		createMessage("message.changecompany",
				new MessageBodyMultipleSelect("Ok, vilket bolag har du idag?",
						new ArrayList<SelectItem>(){{
							add(new SelectOption("If", "message.company.if", false));
							add(new SelectOption("TH", "message.company.th", false));
							add(new SelectOption("LF", "message.company.lf", false));
						}}
				));

		createMessage("message.whyinsurance", new MessageBodyText("Hemförsäkring behöver alla!"));
		
		createMessage("message.whoishedvig", new MessageBodyText("En försäkringsbot!"));
		createMessage("error", new MessageBodyText("Oj nu blev något fel..."));
	}

	public void init(){
		startConversation("message.hello"); // Id of first message
	}

	@Override
	public void recieveMessage(Message m) {
		log.info(m.toString());
		
		switch(m.id){
		case "message.getname": 

			String fName = m.body.text;			
			log.info("Add to context:" + "{NAME}:" + fName);
			userContext.putUserData("{NAME}", fName);
			sendMessage(messageList.get("message.greetings"));
			
			break;

		case "message.greetings": 

			LocalDateTime bDate = ((MessageBodyDatePicker)m.body).date;			
			log.info("Add to context:" + "{BIRTH_DATE}:" + bDate.toString());
			userContext.putUserData("{BIRTH_DATE}", bDate.toString());
			sendMessage(messageList.get("message.bye"));
			
			break;
			
		 default:
			 /*
			  * In a Single select, there is only one trigger event. Set default here to be a link to a new message
			  */
			if(m.body.getClass().equals(MessageBodySingleSelect.class) ){
				
				MessageBodySingleSelect body = (MessageBodySingleSelect)m.body;
				for(SelectItem o : body.choices){
					if(SelectOption.class.isInstance(o) && SelectOption.class.cast(o).selected){
						sendMessage(messageList.get(SelectOption.class.cast(o).value));
					}
				}
			}
			else{
				log.info("Unknown message recieved...");
				sendMessage(messageList.get("error"));
			}
			 
			break;
		}
		
	}

}
