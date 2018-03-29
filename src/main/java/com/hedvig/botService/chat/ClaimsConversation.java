package com.hedvig.botService.chat;

import com.google.common.collect.Lists;
import com.hedvig.botService.enteties.MemberChat;
import com.hedvig.botService.enteties.UserContext;
import com.hedvig.botService.enteties.message.*;
import com.hedvig.botService.serviceIntegration.claimsService.ClaimsService;
import com.hedvig.botService.serviceIntegration.productPricing.ProductPricingService;
import com.hedvig.botService.session.events.ClaimAudioReceivedEvent;
import com.hedvig.botService.session.events.ClaimCallMeEvent;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ClaimsConversation extends Conversation {

    static final String MESSAGE_CLAIMS_START = "message.claims.start";
    static final String MESSAGE_CLAIMS_NOT_ACTIVE = "message.claims.not_active";
    private static final String MESSAGE_CLAIMS_NOT_ACTIVE_CALL_ME = "message.claims.not_active.call_me";
    private static final String MESSAGE_CLAIMS_NOT_ACTIVE_OK = "message.claims.not_active.ok";
    static final String MESSAGE_CLAIM_CALLME = "message.claim.callme";
    /*
                 * Need to be stateless. I.e no variables apart from logger
                 * */
	private static Logger log = LoggerFactory.getLogger(ClaimsConversation.class);
    private final ApplicationEventPublisher eventPublisher;
    private final ClaimsService claimsService;
    private final ProductPricingService productPricingService;
    private final ConversationFactory conversationFactory;

    @Autowired
    ClaimsConversation(
            ApplicationEventPublisher eventPublisher,
            ClaimsService claimsService,
            ProductPricingService productPricingService,
            ConversationFactory conversationFactory) {
		super();
        this.eventPublisher = eventPublisher;
        this.claimsService = claimsService;
        this.productPricingService = productPricingService;
        this.conversationFactory = conversationFactory;


        createMessage(MESSAGE_CLAIMS_START, new MessageBodyParagraph("Okej, det här löser vi på nolltid!"),2000);

        createMessage(MESSAGE_CLAIMS_NOT_ACTIVE,
                new MessageBodySingleSelect("Din försäkring har inte ännu flyttats till Hedvig, du har fortfarande bindningstid kvar hos ditt gamla försäkringsbolag. Så tills vidare skulle jag rekommendera dig att prata med dem. \fBehöver du stöd eller hjälp kan jag så klart be en av mina kollegor att ringa dig?",
                    Lists.newArrayList(
                        new SelectOption("Jag förstår", MESSAGE_CLAIMS_NOT_ACTIVE_OK),
                        new SelectOption("Ring mig", MESSAGE_CLAIMS_NOT_ACTIVE_CALL_ME))
                )
        );
		
        createMessage("message.claim.menu",
                new MessageBodySingleSelect("Är du i en krissituation just nu? Om det är akut så ser jag till att en kollega ringer upp dig",
                        new ArrayList<SelectItem>() {{
                            add(new SelectOption("Ring mig!", MESSAGE_CLAIM_CALLME));
                            add(new SelectOption("Jag vill chatta", "message.claims.chat"));
                        }}
                ));
        
		createMessage(MESSAGE_CLAIM_CALLME, new MessageBodyNumber("Vilket telefonnummer nås du på?"));
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
//        createMessage("message.claims.record.ok3", new MessageBodyParagraph("Tack för att du delat med dig om det som hänt. Ta hand om dig så länge, så hörs vi snart!"),2000);

        createMessage("message.claims.record.ok3",
                new MessageBodySingleSelect("Tack för att du delat med dig om det som hänt. Ta hand om dig så länge, så hörs vi snart!",
                        new ArrayList<SelectItem>() {{
                        	add(new SelectLink("Hem", "onboarding.done", "Dashboard", null, null,  false));
                        }}
                ));

		createMessage("error", new MessageBodyText("Oj nu blev något fel..."));
	}

	public void init(UserContext userContext, String startMessage){
		log.info("Starting claims conversation for user:" + userContext.getMemberId());
		Message m = getMessage(startMessage);
		m.header.fromId = HEDVIG_USER_ID;//new Long(userContext.getMemberId());
		addToChat(m, userContext);
		startConversation(userContext, startMessage); // Id of first message
	}

	@Override
	public void init(UserContext userContext) {
	    if(productPricingService.isMemberInsuranceActive(userContext.getMemberId()) == false) {
            init(userContext, MESSAGE_CLAIMS_NOT_ACTIVE);
            return;
        }

        init(userContext, MESSAGE_CLAIMS_START);
    }

    @Override
    public List<SelectItem> getSelectItemsForAnswer(UserContext uc) {
        return null;
    }

    @Override
    public boolean canAcceptAnswerToQuestion() {
        return false;
    }

    @Override
	public void receiveMessage(UserContext userContext, MemberChat memberChat, Message m) {
		log.info(m.toString());
		
		String nxtMsg = "";
		
		if(!validateReturnType(m,userContext, memberChat)){return;}
		
		switch(m.id){
		case "message.claims.audio":
            nxtMsg = handleAudioReceived(userContext, m);
			
			break;
            case MESSAGE_CLAIM_CALLME:
                userContext.putUserData("{PHONE}", m.body.text);
                addToChat(m, userContext); // Response parsed to nice format
                userContext.completeConversation(this);
                sendCallMeEvent(userContext, m);
                nxtMsg = "message.claims.callme.end";
                break;

            case MESSAGE_CLAIMS_NOT_ACTIVE:
                nxtMsg = handleClaimNotActive(userContext, (MessageBodySingleSelect) m.body);
                if(nxtMsg == null) {
                    return;
                }
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
                   addToChat(m,userContext);
                   nxtMsg = o.value;
               }
           }
       }
       
       completeRequest(nxtMsg,userContext, memberChat);
		
	}

    private void sendCallMeEvent(UserContext userContext, Message m) {
	    val isInsuranceActive = productPricingService.isMemberInsuranceActive(userContext.getMemberId());
        eventPublisher.publishEvent(new ClaimCallMeEvent(
                userContext.getMemberId(),
                userContext.getOnBoardingData().getFirstName(),
                userContext.getOnBoardingData().getFamilyName(),
                m.body.text,
                isInsuranceActive));
    }

    private String handleClaimNotActive(UserContext userContext, MessageBodySingleSelect body) {
        if(body.getSelectedItem().value.equals(MESSAGE_CLAIMS_NOT_ACTIVE_CALL_ME)) {
            return MESSAGE_CLAIM_CALLME;
        }

        userContext.completeConversation(this);
        userContext.startConversation(conversationFactory.createConversation(MainConversation.class));
        return null;
    }

    private String handleAudioReceived(UserContext userContext, Message m) {
        String nxtMsg;
        String audioUrl = ((MessageBodyAudio) m.body).url;
        log.info("Audio recieved with m.body.text:" + m.body.text + " and URL:" + audioUrl);
        m.body.text = "Inspelning klar";

        claimsService.createClaimFromAudio(userContext.getMemberId(), audioUrl);

        this.eventPublisher.publishEvent(new ClaimAudioReceivedEvent(userContext.getMemberId()));

        addToChat(m,userContext); // Response parsed to nice format
        nxtMsg = "message.claims.record.ok";
        return nxtMsg;
    }

    @Override
    public void recieveEvent(EventTypes e, String value, UserContext userContext, MemberChat memberChat){

        switch(e){
            // This is used to let Hedvig say multiple message after another
            case MESSAGE_FETCHED:
                log.info("Message fetched:" + value);
                switch(value){                
                case MESSAGE_CLAIMS_START: completeRequest("message.claims.chat", userContext, memberChat); break;
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
                break;
            default:
                break;
        }
    }

}
