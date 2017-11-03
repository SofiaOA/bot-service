package com.hedvig.botService.chat;

import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hedvig.botService.enteties.*;
import com.hedvig.botService.session.SessionManager;

public class MainConversation extends Conversation {

	private static Logger log = LoggerFactory.getLogger(MainConversation.class);
	private static DateTimeFormatter datetimeformatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private String emoji_hand_ok = new String(new byte[]{(byte)0xF0, (byte)0x9F, (byte)0x91, (byte)0x8C}, Charset.forName("UTF-8"));

	public MainConversation(MemberChat mc, UserContext uc, SessionManager session) {
		super("main.menue", mc,uc,session);
		// TODO Auto-generated constructor stub

		createMessage("hedvig.com",
				new MessageBodySingleSelect("Hej {NAME}, vad vill du göra idag?",
						new ArrayList<SelectItem>(){{
							add(new SelectOption("Det är kris, ring mig!","message.main.callme", false));
							add(new SelectOption("Rapportera en skada","message.main.report", false));
							add(new SelectOption("Rekommendera en vän","message.main.refer", false));
							add(new SelectOption("Ställ en fråga","main.question", false));
						}}
				));
		
		createMessage("message.question.recieved",
				new MessageBodySingleSelect("Tack för din fråga {NAME}, jag åtekommer så snart jag kan?",
						new ArrayList<SelectItem>(){{
							add(new SelectOption("Ok tack!","hedvig.com", false));
						}}
				));
		
		createMessage("message.main.refer.recieved",
				new MessageBodySingleSelect("Då mailar din vän och tipsar om Hedvig." + emoji_hand_ok,
						new ArrayList<SelectItem>(){{
							add(new SelectOption("Bra, gör det","hedvig.com", false));
						}}
				));
		
		createMessage("message.main.callme", new MessageBodyParagraph("Ok, ta det lugnt jag ringer!"));
		
		createMessage("main.question", new MessageBodyText("Vad har du för fråga?"));
		
		createMessage("message.main.refer", new MessageBodyText("Kul! Vad har din vän för emailadress?"));
		
		createMessage("error", new MessageBodyText("Oj nu blev något fel..."));
	}

	@Override
	public void recieveMessage(Message m) {
		log.info(m.toString());
		
		String nxtMsg = "";
		
		switch(m.id){
			case "hedvig.com": {
				SelectItem item = ((MessageBodySingleSelect)m.body).getSelectedItem();
				if(item.value.equals("message.main.report")) {
					nxtMsg = "conversation.done";
					//sessionManager.initClaim(userContext.getMemberId()); // Start claim here
				}
				addToChat(m); // Response parsed to nice format
				break;
			}
		case "message.question": 
			userContext.putUserData("{QUESTION_"+LocalDate.now()+"}", m.body.text);
			addToChat(m); // Response parsed to nice format
			nxtMsg = "message.question.recieved";
			break;

		case "message.main.refer": 
			userContext.putUserData("{REFERAL_"+LocalDate.now()+"}", m.body.text);
			addToChat(m); // Response parsed to nice format
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
                   addToChat(m);
                   nxtMsg = o.value;
               }
           }
       }
       
       completeRequest(nxtMsg);
		
	}

    /*
     * Generate next chat message or ends conversation
     * */
    @Override
    public void completeRequest(String nxtMsg){

        switch(nxtMsg){
            case "conversation.done":
                log.info("conversation complete");
                userContext.completeConversation(this.getClass().getName());
                new ClaimsConversation(this.memberChat, this.userContext, this.sessionManager).init();
				userContext.startOngoingConversation(ClaimsConversation.class.getName());
                //userContext.onboardingComplete(true);
                return;
            case "":
                log.error("I dont know where to go next...");
                nxtMsg = "error";
                break;
        }

        super.completeRequest(nxtMsg);
    }
    
	@Override
	public void init() {
    	log.info("Starting main conversation");
        startConversation("hedvig.com"); // Id of first message
		
	}

	@Override
	public void recieveEvent(EventTypes e, String value) {
		// TODO Auto-generated method stub
		
	}

}
