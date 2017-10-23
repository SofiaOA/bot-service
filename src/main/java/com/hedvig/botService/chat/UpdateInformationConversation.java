package com.hedvig.botService.chat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hedvig.botService.enteties.*;

public class UpdateInformationConversation extends Conversation {

	private static Logger log = LoggerFactory.getLogger(UpdateInformationConversation.class);

	public UpdateInformationConversation(MemberChat mc, UserContext uc) {
		super("info.update", mc,uc);
		// TODO Auto-generated constructor stub

	    /*PERSONAL_INFORMATOIN,
	    FAMILY_MEMBERS,
	    APARTMENT_INFORMATION,
	    BANK_ACCOUNT*/
	    
		createMessage("message.info.update", new MessageBodyText("Ok, vad är det för information du vill uppdatera?\f Beskriv vad det gäller så ändrar jag"));
		createMessage("message.info.complete", new MessageBodyParagraph("Toppen, tack! Jag säger till när informationen är uppdaterad"));

		createMessage("error", new MessageBodyText("Oj nu blev något fel..."));
	}

	@Override
	public void recieveMessage(Message m) {
		log.info(m.toString());
		String nxtMsg = "";
		
		switch(m.id){
		case "message.info.update": 

			nxtMsg = "message.info.complete";
			
			break;
		}
		completeRequest(nxtMsg);
		
	}

	@Override
	public void init() {
    	log.info("Starting main conversation");
        startConversation("message.info.update"); // Id of first message
	}

	@Override
	public void recieveEvent(EventTypes e, String value) {
		// TODO Auto-generated method stub
	}

	@Override
	public void completeRequest(String nxtMsg) {
		addToChat(getMessage(nxtMsg));
	}

}
