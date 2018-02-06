package com.hedvig.botService.chat;

import com.hedvig.botService.enteties.MemberChat;
import com.hedvig.botService.enteties.UserContext;
import com.hedvig.botService.enteties.message.*;
import com.hedvig.botService.serviceIntegration.memberService.MemberService;
import com.hedvig.botService.serviceIntegration.productPricing.ProductPricingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

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

        createMessage("init.asset.claim",
                new MessageBodySingleSelect("Oj vad tråkigt att någon har hänt med din pryl. Självklart tar jag tag i det här",
                    new ArrayList<SelectItem>() {{
                        add(new SelectOption("Ring mig!", "message.claim.callme"));
                        add(new SelectOption("Jag vill chatta", "message.claims.chat"));
                    }}
                ));


        createMessage("message.claims.start", new MessageBodyParagraph("Okej, det här löser vi på nolltid!"),2000);
		
        createMessage("message.claim.menu",
                new MessageBodySingleSelect("Är du i en krissituation just nu? Om det är akut så ser jag till att en kollega ringer upp dig",
                        new ArrayList<SelectItem>() {{
                            add(new SelectOption("Ring mig!", "message.claim.callme"));
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

		createMessage("message.claims.chat", new MessageBodyParagraph("Du ska strax få berätta vad som hänt genom att spela in ett röstmeddelande"),2000);
		createMessage("message.claims.chat2", new MessageBodyParagraph("Först behöver du bara bekräfta detta"),2000);

        createMessage("message.claim.promise",
                new MessageBodySingleSelect("HEDVIGS HEDERSLÖFTE\nJag förstår att Hedvig bygger på tillit.\nJag lovar att berätta om händelsen precis som den var, och bara ta ut den ersättning jag har rätt till. Tar jag ut mer än så inser jag att det drabbar en välgörenhetsorganisation",        		
                //new MessageBodySingleSelect("HEDVIGS HEDERSLÖFTE\nJag vet att Hedvig bygger på tillit medlemmar emellan.\nJag lovar att berätta om händelsen precis som den var, och bara ta ut den ersättning jag har rätt till ur vår gemensamma medlemspott.",
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
        createMessage("message.claims.record.ok2", new MessageBodyParagraph("Jag återkommer till dig om jag behöver något mer, eller för att meddela att jag kan betala ut ersättning direkt"),2000);
        createMessage("message.claims.record.ok3", new MessageBodyParagraph("Tack för att du delat med dig om det som hänt. Ta hand om dig så länge, så hörs vi snart!"),2000);

        createMessage("message.claims.record.ok3",
                new MessageBodySingleSelect("Tack för att du delat med dig om det som hänt. Ta hand om dig så länge, så hörs vi snart!",
                        new ArrayList<SelectItem>() {{
                        	add(new SelectLink("Hem", "onboarding.done", "Dashboard", null, null,  false));
                        }}
                ));
        
		createMessage("error", new MessageBodyText("Oj nu blev något fel..."));
	}

	public void init(UserContext userContext, MemberChat memberChat, String startMessage){
		log.info("Starting claims conversation for user:" + userContext.getMemberId());
		Message m = getMessage(startMessage);
		m.header.fromId = HEDVIG_USER_ID;//new Long(userContext.getMemberId());
		addToChat(m, userContext, memberChat);
		startConversation(userContext, memberChat, startMessage); // Id of first message
	}

	public void init(UserContext userContext, MemberChat memberChat) {
	    init(userContext, memberChat, "message.claims.start");
    }

	@Override
	public void recieveMessage(UserContext userContext, MemberChat memberChat, Message m) {
		log.info(m.toString());
		
		String nxtMsg = "";
		
		if(!validateReturnType(m,userContext, memberChat)){return;}
		
		switch(m.id){
		case "message.claims.audio": 
			log.info("Audio recieved with m.body.text:" + m.body.text + " and URL:" + ((MessageBodyAudio)m.body).URL);
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
                case "message.claims.start": completeRequest("message.claims.chat", userContext, memberChat); break;
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
