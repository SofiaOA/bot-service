package com.hedvig.botService.chat;

import com.hedvig.botService.dataTypes.EmailAdress;
import com.hedvig.botService.enteties.MemberChat;
import com.hedvig.botService.enteties.UserContext;
import com.hedvig.botService.enteties.message.*;
import com.hedvig.botService.serviceIntegration.productPricing.ProductPricingService;
import com.hedvig.botService.session.events.QuestionAskedEvent;
import com.hedvig.botService.session.events.RequestPhoneCallEvent;
import feign.FeignException;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;
import java.util.ArrayList;

@Component
public class MainConversation extends Conversation {

	public static final String MESSAGE_HEDVIG_COM = "hedvig.com";
	public static final String MESSAGE_QUESTION_RECIEVED = "message.question.recieved";
	public static final String MESSAGE_MAIN_REFER_RECIEVED = "message.main.refer.recieved";
	public static final String MESSAGE_MAIN_END = "message.main.end";
	public static final String MESSAGE_MAIN_CALLME = "message.main.callme";
	public static final String MESSAGE_MAIN_QUESTION = "main.question";
	public static final String MESSAGE_MAIN_REFER = "message.main.refer";
	public static final String MESSAGE_ERROR = "error";

	private static Logger log = LoggerFactory.getLogger(MainConversation.class);
	private final ConversationFactory conversationFactory;
	private final ProductPricingService productPricingService;
	private final ApplicationEventPublisher eventPublisher;
	private final Environment springEnvironment;
	private String emoji_hand_ok = new String(new byte[]{(byte)0xF0, (byte)0x9F, (byte)0x91, (byte)0x8C}, Charset.forName("UTF-8"));



    @Autowired
	public MainConversation(ProductPricingService productPricingService, ConversationFactory conversationFactory, ApplicationEventPublisher eventPublisher, Environment springEnvironment) {
		super("main.menue");
		this.productPricingService = productPricingService;
		this.conversationFactory = conversationFactory;
		this.eventPublisher = eventPublisher;
		this.springEnvironment = springEnvironment;
		// TODO Auto-generated constructor stub

		createMessage(MESSAGE_HEDVIG_COM,
				new MessageBodySingleSelect("Hej {NAME}, vad vill du göra idag?",
						new ArrayList<SelectItem>(){{
							add(new SelectOption("Rapportera en skada","message.main.report", false));
							add(new SelectOption("Ring mig!","message.main.callme", false));
							add(new SelectOption("Jag har en fråga","main.question", false));
							if(!springEnvironment.acceptsProfiles("production")) {
								add(new SelectOption("Välj autogiro konto", "message.main.refer", false));
							}
						}}
				));
		
		createMessage(MESSAGE_QUESTION_RECIEVED,
				new MessageBodySingleSelect("Tack {NAME}, jag återkommer så snart jag kan med svar på din fråga",
						new ArrayList<SelectItem>(){{
							add(new SelectLink("Hem", "onboarding.done", "Dashboard", null, null,  false));
							//add(new SelectOption("Ok tack!","hedvig.com", false));
						}}
				));

		createMessage(MESSAGE_MAIN_REFER_RECIEVED,
				new MessageBodySingleSelect("Då mailar din vän och tipsar om Hedvig" + emoji_hand_ok,
						new ArrayList<SelectItem>(){{
							add(new SelectLink("Hem", "onboarding.done", "Dashboard", null, null,  false));
							//add(new SelectOption("Bra, gör det","hedvig.com", false));
						}}
				));
		
        createMessage(MESSAGE_MAIN_END,
                new MessageBodySingleSelect("Tack. Jag ringer upp dig så snart jag kan",
                        new ArrayList<SelectItem>() {{
                        	add(new SelectLink("Hem", "onboarding.done", "Dashboard", null, null,  false));
                        }}
                ));
        
		createMessage(MESSAGE_MAIN_CALLME, new MessageBodyNumber("Ok, ta det lugnt! Vad når jag dig på för nummer?"));
		
		createMessage(MESSAGE_MAIN_QUESTION, new MessageBodyText("Självklart, vad kan jag hjälpa dig med?"));
		
		createMessage(MESSAGE_MAIN_REFER, new MessageBodyText("Kul! Vad har din vän för emailadress?"));
		setExpectedReturnType("message.main.refer", new EmailAdress());
		
		createMessage(MESSAGE_ERROR, new MessageBodyText("Oj nu blev något fel..."));
	}

	@Override
	public void receiveMessage(UserContext userContext, MemberChat memberChat, Message m) {
		log.info(m.toString());
		
		String nxtMsg = "";
		
		if(!validateReturnType(m,userContext, memberChat)){return;}
		
		switch(m.id){
			case MESSAGE_HEDVIG_COM: {
				SelectItem item = ((MessageBodySingleSelect)m.body).getSelectedItem();
				m.body.text = item.text;
				if(item.value.equals("message.main.report")) {
					nxtMsg = "conversation.done";
					//sessionManager.initClaim(userContext.getMemberId()); // Start claim here
				}
				else if(item.value.equals("message.main.refer")) {
					userContext.completeConversation(this.getClass().getName()); // TODO: End conversation in better way
					userContext.startConversation(conversationFactory.createConversation(TrustlyConversation.class));
					return;
				}
				addToChat(m, userContext); // Response parsed to nice format
				break;
			}
		case "message.main.callme": 
			userContext.putUserData("{PHONE_"+ new LocalDate().toString() + "}", m.body.text);
			eventPublisher.publishEvent(new RequestPhoneCallEvent(userContext.getMemberId(), m.body.text, userContext.getOnBoardingData().getFirstName(), userContext.getOnBoardingData().getFamilyName()));
			nxtMsg = "message.main.end";
			addToChat(m, userContext); // Response parsed to nice format
			userContext.completeConversation(this.getClass().getName()); // TODO: End conversation in better way
			break;
		case MESSAGE_MAIN_QUESTION:
			nxtMsg = handleQuestion(userContext, m);
			break;
		case "message.main.refer": 
			userContext.putUserData("{REFERAL}", m.body.text);
			addToChat(m, userContext); // Response parsed to nice format
			nxtMsg = "message.main.refer.recieved";

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
                   addToChat(m, userContext);
                   nxtMsg = o.value;
               }
           }
       }
       
       completeRequest(nxtMsg, userContext, memberChat);
		
	}

	public String handleQuestion(UserContext userContext, Message m) {
		String nxtMsg;
		final String question = m.body.text;
		userContext.putUserData("{QUESTION_"+ new LocalDate().toString() + "}", question);
		addToChat(m, userContext); // Response parsed to nice format
		eventPublisher.publishEvent(new QuestionAskedEvent(userContext.getMemberId(), question));
		nxtMsg = MESSAGE_QUESTION_RECIEVED;
		userContext.completeConversation(this.getClass().getName()); // TODO: End conversation in better way
		return nxtMsg;
	}

	/*
     * Generate next chat message or ends conversation
     * */
    @Override
    public void completeRequest(String nxtMsg, UserContext userContext, MemberChat memberChat){

        switch(nxtMsg){
            case "conversation.done":
                log.info("conversation complete");
                userContext.completeConversation(this.getClass().getName());

				userContext.startConversation(conversationFactory.createConversation(ClaimsConversation.class));

                return;
            case "":
                log.error("I dont know where to go next...");
                nxtMsg = "error";
                break;
        }

        super.completeRequest(nxtMsg, userContext, memberChat);
    }

    @Override
	void addToChat(Message m, UserContext userContext) {
		if(m.body.getClass() == MessageBodySingleSelect.class) {
			MessageBodySingleSelect mss = (MessageBodySingleSelect) m.body;

			// Do not show report claim option when user is not active
			Boolean isActive = userHasActiveInsurance(userContext);

			if (!isActive) {
				mss.removeItemIf(x -> x instanceof SelectOption && ((SelectOption) x).value.equals("message.main.report"));

			}
		}

		super.addToChat(m,userContext);
	}

	private Boolean userHasActiveInsurance(UserContext userContext) {
		Boolean isActive = false;
		try{
			isActive = productPricingService.getInsuranceStatus(userContext.getMemberId()).equals("ACTIVE");
		}catch(FeignException ex){
			if(ex.status() != 404) {
				log.error(ex.getMessage());
			}
		}catch (Exception ex) {
			log.error(ex.getMessage());
		}
		return isActive;
	}
    
	@Override
	public void init(UserContext userContext) {
    	log.info("Starting main conversation");
        startConversation(userContext, MESSAGE_HEDVIG_COM); // Id of first message
	}

	@Override
	public void init(UserContext userContext, String startMessage) {
    	log.info("Starting main conversation with message:" + startMessage);
        startConversation(userContext, MESSAGE_HEDVIG_COM); // Id of first message
	}

}
