package com.hedvig.botService.chat;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hedvig.botService.enteties.*;
import com.hedvig.botService.session.SessionManager;

public class ClaimsConversation extends Conversation {

	private static Logger log = LoggerFactory.getLogger(ClaimsConversation.class);

	public ClaimsConversation(MemberChat mc, UserContext uc, SessionManager session) {
		super("claims", mc,uc, session);
		// TODO Auto-generated constructor stub

		createMessage("message.claims.start", new MessageBodyParagraph("Jag förstår, hoppas du mår ok under omständigheterna. Självklart tar jag tag i det här"), "h_symbol",2000);
		
        createMessage("message.claim.menu",
                new MessageBodySingleSelect("Är du i en krissituation just nu? Om det är akut så ser jag till att en kollega ringer upp dig",
                        new ArrayList<SelectItem>() {{
                            add(new SelectOption("Det är kris, ring mig!", "message.claim.callme"));
                            add(new SelectOption("Jag vill chatta", "message.claims.chat"));
                        }}
                ), "h_symbol");
        
		createMessage("message.claim.callme", new MessageBodyText("Vilket telefonnummer nås du på?"));
		createMessage("message.claims.callme.end", new MessageBodyParagraph("Tack! En kollega ringer dig så snart som möjligt"), "h_symbol",2000);

		createMessage("message.claims.chat", new MessageBodyParagraph("Ok! Då kommer du strax få berätta vad som hänt genom att spela in ett röstmeddelande"), "h_symbol",2000);
		createMessage("message.claims.chat2", new MessageBodyParagraph("Först vill jag bara be dig skriva under detta"), "h_symbol",2000);

        createMessage("message.claim.promise",
                new MessageBodySingleSelect("HEDVIGS HEDERSLÖFTE\nJag vet att Hedvig bygger på tillit medlemmar emellan.\nJag lovar att berätta om händelsen precis som den var, och bara ta ut den ersättning jag har rätt till ur vår gemensamma medlemspott.",
                        new ArrayList<SelectItem>() {{
                            add(new SelectOption("Jag lovar!", "message.claims.ok"));
                        }}
                ), "h_symbol");
        
        createMessage("message.claims.ok", new MessageBodyParagraph("Tusen tack!"), "h_symbol",2000);
        createMessage("message.claims.record", new MessageBodyParagraph("Berätta vad som har hänt genom att spela in ett röstmeddelande"), "h_symbol",2000);
        createMessage("message.claims.record2", new MessageBodyParagraph("Ju mer detaljer du ger, desto snabbare hjälp kan jag ge. Så om du svarar på dessa frågor är vi en god bit på väg: "), "h_symbol",2000);
        createMessage("message.claims.record3", new MessageBodyParagraph("Vad har hänt?"), "h_symbol",2000);
        createMessage("message.claims.record4", new MessageBodyParagraph("Var och när hände det?"), "h_symbol",2000);
        createMessage("message.claims.record5", new MessageBodyParagraph("Vad eller vem drabbades?"), "h_symbol",2000);
        
        createMessage("message.claims.audio", new MessageBodyAudio("Starta inspelning", "/claims/fileupload"), "h_symbol",2000);
        
        createMessage("message.claims.record.ok", new MessageBodyParagraph("Tack! Det är allt jag behöver just nu"), "h_symbol",2000);
        createMessage("message.claims.record.ok2", new MessageBodyParagraph("Jag återkommer till dig här i chatten om jag behöver något mer, eller för att meddela att jag kan betala ut ersättning direkt"), "h_symbol",2000);
        createMessage("message.claims.record.ok3", new MessageBodyParagraph("Tack för att du delat med dig om det som hänt. Ta hand om dig så länge, så hörs vi snart!"), "h_symbol",2000);

        createMessage("message.claims.record.ok3",
                new MessageBodySingleSelect("Tack för att du delat med dig om det som hänt. Ta hand om dig så länge, så hörs vi snart!",
                        new ArrayList<SelectItem>() {{
                        	add(new SelectLink("Hem", "onboarding.done", "Dashboard", null, null,  false));
                        }}
                ), "h_symbol");
        
		createMessage("error", new MessageBodyText("Oj nu blev något fel..."));
	}

	public void init(String hid){
		log.info("Starting claims conversation for user:" + hid);
		Message m = getMessage("message.claims.init");
		m.header.fromId = new Long(hid);
		addToChat(m);
		startConversation("message.claims.whathappened"); // Id of first message
	}

	@Override
	public void recieveMessage(Message m) {
		log.info(m.toString());
		
		String nxtMsg = "";
		
		switch(m.id){
		case "message.claims.audio": 

			// TODO: Send to claims service!
			m.body.text = "Inspelning klar";
			addToChat(m); // Response parsed to nice format
			nxtMsg = "message.claims.record.ok";
			
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
	public void init() {
		// TODO Auto-generated method stub
		startConversation("message.claims.start");
	}

    @Override
    public void recieveEvent(EventTypes e, String value){

        switch(e){
            // This is used to let Hedvig say multiple message after another
            case MESSAGE_FETCHED:
                log.info("Message fetched:" + value);
                switch(value){                
                case "message.claims.start": completeRequest("message.claim.menu"); break;
                case "message.claims.chat": completeRequest("message.claims.chat2"); break;
                case "message.claims.chat2": completeRequest("message.claim.promise"); break;
                case "message.claims.ok": completeRequest("message.claims.record"); break;
                case "message.claims.record": completeRequest("message.claims.record2"); break;
                case "message.claims.record2": completeRequest("message.claims.record3"); break;
                case "message.claims.record3": completeRequest("message.claims.record4"); break;
                case "message.claims.record4": completeRequest("message.claims.record5"); break;
                case "message.claims.record5": completeRequest("message.claims.audio"); break;
                case "message.claims.record.ok": completeRequest("message.claims.record.ok2"); break;
                case "message.claims.record.ok2": completeRequest("message.claims.record.ok3"); break;
                }
        }
    }

}
