package com.hedvig.botService.chat;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hedvig.botService.enteties.*;
import com.hedvig.botService.enteties.message.Message;
import com.hedvig.botService.enteties.message.MessageBodyParagraph;
import com.hedvig.botService.enteties.message.MessageBodySingleSelect;
import com.hedvig.botService.enteties.message.MessageBodyText;
import com.hedvig.botService.enteties.message.SelectItem;
import com.hedvig.botService.serviceIntegration.memberService.MemberService;
import com.hedvig.botService.serviceIntegration.productPricing.ProductPricingService;
import com.hedvig.botService.session.SessionManager;

@Component
public class UpdateInformationConversation extends Conversation {

	/*
	 * Need to be stateless. I.e no variables apart from logger
	 * */
	private static Logger log = LoggerFactory.getLogger(UpdateInformationConversation.class);

	@Autowired
	public UpdateInformationConversation(MemberService memberService, ProductPricingService productPricingClient) {
		super("info.update", memberService, productPricingClient);

		createMessage("message.info.update.email", new MessageBodyText("Ok, vad har du för mailadress?"));
		createMessage("message.info.update", new MessageBodyText("Ok, vad är det för information du vill uppdatera?\f Beskriv vad det gäller så ändrar jag"));
		createMessage("message.info.complete", new MessageBodyParagraph("Toppen, tack! Jag säger till när informationen är uppdaterad"));

		createMessage("error", new MessageBodyText("Oj nu blev något fel..."));

	}

	@Override
	public void recieveMessage(UserContext userContext, MemberChat memberChat, Message m) {
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
                   addToChat(m, userContext, memberChat);
                   nxtMsg = o.value;
               }
           }
       }
       
       completeRequest(nxtMsg, userContext, memberChat);
		
	}

    @Override
    public void completeRequest(String nxtMsg, UserContext userContext, MemberChat memberChat){

		switch(nxtMsg){
			case "message.info.complete":
				log.info("Update conversation complete");
				userContext.completeConversation(this.getClass().getName());
				//userContext.onboardingComplete(true);
				break;
			}

			super.completeRequest(nxtMsg, userContext, memberChat);
	}

	public void init(UserContext userContext, MemberChat memberChat, String startMessage) {
    	log.info("Starting main conversation");
        startConversation(userContext, memberChat, startMessage); // Id of first message
	}

	public void init(UserContext userContext, MemberChat memberChat) {
		log.info("Starting main conversation with: message.info.update");
		startConversation(userContext, memberChat, "message.info.update"); // Id of first message
	}


}
