package com.hedvig.botService.chat;

import java.util.ArrayList;

import com.hedvig.botService.enteties.message.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.hedvig.botService.enteties.*;
import com.hedvig.botService.serviceIntegration.memberService.MemberService;
import com.hedvig.botService.serviceIntegration.productPricing.ProductPricingService;
import com.hedvig.botService.session.SessionManager;

@Component
public class ClaimsConversation extends Conversation {

	/*
	 * Need to be stateless. I.e no variables apart from logger
	 * */
	private static Logger log = LoggerFactory.getLogger(ClaimsConversation.class);

	@Autowired
	public ClaimsConversation(MemberService memberService, ProductPricingService productPricingClient) {
		super("claims", memberService, productPricingClient);
		// TODO Auto-generated constructor stub

		createMessage("message.claims.start", new MessageBodyParagraph("Jag förstår, hoppas du mår ok under omständigheterna. Självklart tar jag tag i det här"),2000);
		
        createMessage("message.claim.menu",
                new MessageBodySingleSelect("Är du i en krissituation just nu? Om det är akut så ser jag till att en kollega ringer upp dig",
                        new ArrayList<SelectItem>() {{
                            add(new SelectOption("Det är kris, ring mig!", "message.claim.callme"));
                            add(new SelectOption("Jag vill chatta", "message.claims.chat"));
                        }}
                ));
        
		createMessage("message.claim.callme", new MessageBodyNumber("Vilket telefonnummer nås du på?"));
        createMessage("message.claims.callme.end",
                new MessageBodySingleSelect("Tack! En kollega ringer dig så snart som möjligt",
                        new ArrayList<SelectItem>() {{
                            add(new SelectLink("Hem", "onboarding.done", "Dashboard", null, null,  false));
                        }}
                ));

		createMessage("message.claims.chat", new MessageBodyParagraph("Ok! Då kommer du strax få berätta vad som hänt genom att spela in ett röstmeddelande"),2000);
		createMessage("message.claims.chat2", new MessageBodyParagraph("Först vill jag bara be dig skriva under detta"),2000);

        createMessage("message.claim.promise",
                new MessageBodySingleSelect("HEDVIGS HEDERSLÖFTE\nJag vet att Hedvig bygger på tillit medlemmar emellan.\nJag lovar att berätta om händelsen precis som den var, och bara ta ut den ersättning jag har rätt till ur vår gemensamma medlemspott.",
                        new ArrayList<SelectItem>() {{
                            add(new SelectOption("Jag lovar!", "message.claims.ok"));
                        }}
                ));
        
        createMessage("message.claims.ok", new MessageBodyParagraph("Tusen tack!"),2000);
        createMessage("message.claims.record", new MessageBodyParagraph("Berätta vad som har hänt genom att spela in ett röstmeddelande"),2000);
        createMessage("message.claims.record2", new MessageBodyParagraph("Ju mer detaljer du ger, desto snabbare hjälp kan jag ge. Så om du svarar på dessa frågor är vi en god bit på väg: "),2000);
        createMessage("message.claims.record3", new MessageBodyParagraph("Vad har hänt?"),2000);
        createMessage("message.claims.record4", new MessageBodyParagraph("Var och när hände det?"),2000);
        createMessage("message.claims.record5", new MessageBodyParagraph("Vad eller vem drabbades?"),2000);
        
        createMessage("message.claims.audio", new MessageBodyAudio("Starta inspelning", "/claims/fileupload"),2000);
        
        createMessage("message.claims.record.ok", new MessageBodyParagraph("Tack! Det är allt jag behöver just nu"),2000);
        createMessage("message.claims.record.ok2", new MessageBodyParagraph("Jag återkommer till dig här i chatten om jag behöver något mer, eller för att meddela att jag kan betala ut ersättning direkt"),2000);
        createMessage("message.claims.record.ok3", new MessageBodyParagraph("Tack för att du delat med dig om det som hänt. Ta hand om dig så länge, så hörs vi snart!"),2000);

        createMessage("message.claims.record.ok3",
                new MessageBodySingleSelect("Tack för att du delat med dig om det som hänt. Ta hand om dig så länge, så hörs vi snart!",
                        new ArrayList<SelectItem>() {{
                        	add(new SelectLink("Hem", "onboarding.done", "Dashboard", null, null,  false));
                        }}
                ));
        
		createMessage("error", new MessageBodyText("Oj nu blev något fel..."));
	}

	public void init(UserContext userContext, MemberChat memberChat){
		log.info("Starting claims conversation for user:" + userContext.getMemberId());
		Message m = getMessage("message.claims.start");
		m.header.fromId = HEDVIG_USER_ID;//new Long(userContext.getMemberId());
		addToChat(m, userContext, memberChat);
		startConversation(userContext, memberChat, "message.claims.start"); // Id of first message
	}

	@Override
	public void recieveMessage(UserContext userContext, MemberChat memberChat, Message m) {
		log.info(m.toString());
		
		String nxtMsg = "";
		
		switch(m.id){
		case "message.claims.audio": 

			// TODO: Send to claims service!
			m.body.text = "Inspelning klar";
			addToChat(m,userContext, memberChat); // Response parsed to nice format
			nxtMsg = "message.claims.record.ok";
			
			break;
            case "message.claim.callme":
                userContext.putUserData("{PHONE}", m.body.text);
                addToChat(m, userContext, memberChat); // Response parsed to nice format
                userContext.completeConversation(this.getClass().getName()); // TODO: End conversation in better way
                nxtMsg = "message.claims.callme.end";
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
                   addToChat(m,userContext, memberChat);
                   nxtMsg = o.value;
               }
           }
       }
       
       completeRequest(nxtMsg,userContext, memberChat);
		
	}

    @Override
    public void recieveEvent(EventTypes e, String value, UserContext userContext, MemberChat memberChat){

        switch(e){
            // This is used to let Hedvig say multiple message after another
            case MESSAGE_FETCHED:
                log.info("Message fetched:" + value);
                switch(value){                
                case "message.claims.start": completeRequest("message.claim.menu", userContext, memberChat); break;
                case "message.claims.chat": completeRequest("message.claims.chat2", userContext, memberChat); break;
                case "message.claims.chat2": completeRequest("message.claim.promise", userContext, memberChat); break;
                case "message.claims.ok": completeRequest("message.claims.record", userContext, memberChat); break;
                case "message.claims.record": completeRequest("message.claims.record2", userContext, memberChat); break;
                case "message.claims.record2": completeRequest("message.claims.record3", userContext, memberChat); break;
                case "message.claims.record3": completeRequest("message.claims.record4", userContext, memberChat); break;
                case "message.claims.record4": completeRequest("message.claims.record5", userContext, memberChat); break;
                case "message.claims.record5": completeRequest("message.claims.audio", userContext, memberChat); break;
                case "message.claims.record.ok": completeRequest("message.claims.record.ok2", userContext, memberChat); break;
                case "message.claims.record.ok2": completeRequest("message.claims.record.ok3", userContext, memberChat); break;
                }
        }
    }

}
