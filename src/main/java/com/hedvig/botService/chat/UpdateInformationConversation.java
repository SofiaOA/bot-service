package com.hedvig.botService.chat;

import com.hedvig.botService.enteties.MemberChat;
import com.hedvig.botService.enteties.UserContext;
import com.hedvig.botService.enteties.message.*;
import com.hedvig.botService.serviceIntegration.memberService.MemberService;
import com.hedvig.botService.serviceIntegration.productPricing.ProductPricingService;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
		
		createMessage("message.info.update.safety", new MessageBodyText("Okej! Har du kanske skaffat en ny trygghetshöjare eller ändrat om något hemma? Skriv bara här vad som har hänt så uppdaterar jag din info"));
		createMessage("message.info.update.payment", new MessageBodyText("Okej, vill du kanske flytta autogiro till ett annat konto? Ange bank och kontonummer så tar jag hand om det"));
		createMessage("message.info.update", new MessageBodyText("Okej, skriv bara här vad du vill uppdatera så fixar jag det! Har du kanske flyttat, eller vill lägga till eller ta bort en person ur din försäkring är det bara att ange det"));
		createMessage("message.info.complete", new MessageBodyText("Toppen tack, jag ändrar"));

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
		case "message.info.update.payment": 
			userContext.putUserData("{PAYMENT_UPDATE_"+LocalDate.now()+"}", m.body.text);
			nxtMsg = "message.info.complete";
			break;
		case "message.info.update.safety": 
			userContext.putUserData("{SAFETY_UPDATE_"+LocalDate.now()+"}", m.body.text);
			nxtMsg = "message.info.complete";
			break;
		case "message.info.update.email": 
			userContext.putUserData("{EMAIL_UPDATE_"+LocalDate.now()+"}", m.body.text);
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

                   nxtMsg = o.value;
               }
           }
       }

       addToChat(m, userContext, memberChat);
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
