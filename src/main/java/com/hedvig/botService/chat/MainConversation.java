package com.hedvig.botService.chat;

import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hedvig.botService.dataTypes.EmailAdress;
import com.hedvig.botService.enteties.*;
import com.hedvig.botService.enteties.message.Message;
import com.hedvig.botService.enteties.message.MessageBodyNumber;
import com.hedvig.botService.enteties.message.MessageBodySingleSelect;
import com.hedvig.botService.enteties.message.MessageBodyText;
import com.hedvig.botService.enteties.message.SelectItem;
import com.hedvig.botService.enteties.message.SelectLink;
import com.hedvig.botService.enteties.message.SelectOption;
import com.hedvig.botService.serviceIntegration.memberService.MemberService;
import com.hedvig.botService.serviceIntegration.productPricing.ProductPricingService;
import com.hedvig.botService.session.SessionManager;

@Component
public class MainConversation extends Conversation {

	private static Logger log = LoggerFactory.getLogger(MainConversation.class);
	private static DateTimeFormatter datetimeformatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private String emoji_hand_ok = new String(new byte[]{(byte)0xF0, (byte)0x9F, (byte)0x91, (byte)0x8C}, Charset.forName("UTF-8"));

    @Autowired
	public MainConversation(MemberService memberService, ProductPricingService productPricingClient) {
		super("main.menue", memberService, productPricingClient);
		// TODO Auto-generated constructor stub

		createMessage("hedvig.com",
				new MessageBodySingleSelect("Hej {NAME}, vad vill du göra idag?",
						new ArrayList<SelectItem>(){{
							add(new SelectOption("Rapportera en skada","message.main.report", false));
							add(new SelectOption("Det är kris, ring mig!","message.main.callme", false));
							add(new SelectOption("Ställ en fråga","main.question", false));
							add(new SelectOption("Rekommendera en vän","message.main.refer", false));							
						}}
				));
		
		createMessage("message.question.recieved",
				new MessageBodySingleSelect("Tack för din fråga {NAME}, jag återkommer så snart jag kan?",
						new ArrayList<SelectItem>(){{
							add(new SelectLink("Hem", "onboarding.done", "Dashboard", null, null,  false));
							//add(new SelectOption("Ok tack!","hedvig.com", false));
						}}
				));
		
		createMessage("message.main.refer.recieved",
				new MessageBodySingleSelect("Då mailar din vän och tipsar om Hedvig." + emoji_hand_ok,
						new ArrayList<SelectItem>(){{
							add(new SelectLink("Hem", "onboarding.done", "Dashboard", null, null,  false));
							//add(new SelectOption("Bra, gör det","hedvig.com", false));
						}}
				));
		
        createMessage("message.main.end",
                new MessageBodySingleSelect("Tack, jag ringer!",
                        new ArrayList<SelectItem>() {{
                        	add(new SelectLink("Hem", "onboarding.done", "Dashboard", null, null,  false));
                        }}
                ), "h_symbol");
        
		createMessage("message.main.callme", new MessageBodyNumber("Ok, ta det lugnt! Vad når jag dig på för nummer?"));
		
		createMessage("main.question", new MessageBodyText("Vad har du för fråga?"));
		
		createMessage("message.main.refer", new MessageBodyText("Kul! Vad har din vän för emailadress?"));
		setExpectedReturnType("message.main.refer", new EmailAdress());
		
		createMessage("error", new MessageBodyText("Oj nu blev något fel..."));
	}

	@Override
	public void recieveMessage(UserContext userContext, MemberChat memberChat, Message m) {
		log.info(m.toString());
		
		String nxtMsg = "";
		
		switch(m.id){
			case "hedvig.com": {
				SelectItem item = ((MessageBodySingleSelect)m.body).getSelectedItem();
				m.body.text = item.text;
				if(item.value.equals("message.main.report")) {
					nxtMsg = "conversation.done";
					//sessionManager.initClaim(userContext.getMemberId()); // Start claim here
				}
				addToChat(m, userContext, memberChat); // Response parsed to nice format
				break;
			}
		case "message.main.callme": 
			userContext.putUserData("{PHONE}", m.body.text);
			nxtMsg = "message.main.end";
			addToChat(m, userContext, memberChat); // Response parsed to nice format
			userContext.completeConversation(this.getClass().getName()); // TODO: End conversation in better way
			break;
		case "main.question":
			userContext.putUserData("{QUESTION}", m.body.text);
			addToChat(m, userContext, memberChat); // Response parsed to nice format
			nxtMsg = "message.question.recieved";
			userContext.completeConversation(this.getClass().getName()); // TODO: End conversation in better way
			break;
		case "message.main.refer": 
			userContext.putUserData("{REFERAL}", m.body.text);
			addToChat(m, userContext, memberChat); // Response parsed to nice format
			nxtMsg = "message.main.refer.recieved";
			userContext.completeConversation(this.getClass().getName()); // TODO: End conversation in better way
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

    /*
     * Generate next chat message or ends conversation
     * */
    @Override
    public void completeRequest(String nxtMsg, UserContext userContext, MemberChat memberChat){

        switch(nxtMsg){
            case "conversation.done":
                log.info("conversation complete");
                userContext.completeConversation(this.getClass().getName());
                //new ClaimsConversation(memberService, productPricingClient).init(userContext, memberChat);
				userContext.startConversation(new ClaimsConversation(memberService, productPricingClient));
                //userContext.onboardingComplete(true);
                return;
            case "":
                log.error("I dont know where to go next...");
                nxtMsg = "error";
                break;
        }

        super.completeRequest(nxtMsg, userContext, memberChat);
    }
    
	@Override
	public void init(UserContext userContext, MemberChat memberChat) {
    	log.info("Starting main conversation");
        startConversation(userContext, memberChat, "hedvig.com"); // Id of first message
		
	}

}
