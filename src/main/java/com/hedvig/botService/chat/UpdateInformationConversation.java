package com.hedvig.botService.chat;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hedvig.botService.enteties.*;
import com.hedvig.botService.session.SessionManager;

public class UpdateInformationConversation extends Conversation {

	private static Logger log = LoggerFactory.getLogger(UpdateInformationConversation.class);
	private String startMessage = "message.info.update";
	
	public UpdateInformationConversation(MemberChat mc, UserContext uc, SessionManager session) {
		super("info.update", mc,uc, session);
		
		// TODO Auto-generated constructor stub

	    /*PERSONAL_INFORMATOIN,
	    FAMILY_MEMBERS,
	    APARTMENT_INFORMATION,
	    BANK_ACCOUNT
	    SAFETY_INCREASERS*/
	    
		createMessage("message.info.update.email", new MessageBodyText("Ok, vad har du för mailadress?"));
		createMessage("message.info.update", new MessageBodyText("Ok, vad är det för information du vill uppdatera?\f Beskriv vad det gäller så ändrar jag"));
		createMessage("message.info.complete", new MessageBodyParagraph("Toppen, tack! Jag säger till när informationen är uppdaterad"));

		createMessage("error", new MessageBodyText("Oj nu blev något fel..."));

	}

	public UpdateInformationConversation(MemberChat mc, UserContext uc, SessionManager session, String startMessage) {
		this(mc,uc, session);
		this.startMessage = startMessage;		
	}	
	
	@Override
	public void recieveMessage(Message m) {
		log.info(m.toString());
		String nxtMsg = "";
		
		switch(m.id){
		case "message.info.update": 
			userContext.putUserData("{INFO_UPDATE_"+LocalDate.now()+"}", m.body.text);
			nxtMsg = "message.info.complete";
			break;
		case "message.info.update.email": 
			userContext.putUserData("{EMAIL}", m.body.text);
			nxtMsg = "message.info.complete";
			break;
		}
		
        /*
	  * In a Single select, there is only one trigger event. Set default here to be a link to a new message
	  */
       if (nxtMsg.equals("") && m.body.getClass().equals(MessageBodySingleSelect.class)) {

           MessageBodySingleSelect body1 = (MessageBodySingleSelect) m.body;
           for (SelectItem o : body1.choices) {
               if(o.selected) {
                   m.body.text = o.text;
                   addToChat(m);
                   nxtMsg = o.value;
               }
           }
       }
       
       completeRequest(nxtMsg);
		
	}

    @Override
	public void completeRequest(String nxtMsg){

		switch(nxtMsg){
			case "message.info.complete":
				log.info("Update conversation complete");
				userContext.completeConversation(this.getClass().getName());
				//userContext.onboardingComplete(true);
				break;
			}

			super.completeRequest(nxtMsg);
	}
    
	@Override
	public void init() {
    	log.info("Starting main conversation");
        startConversation(startMessage); // Id of first message
	}

	@Override
	public void recieveEvent(EventTypes e, String value) {
		// TODO Auto-generated method stub
	}

}
