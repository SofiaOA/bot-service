package com.hedvig.botService.session;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/*
 * The session manager is the main controller class for the chat service. It contains all user sessions with chat histories, context etc
 * It is a singleton accessed through the request controller
 * */

import java.util.List;

import com.hedvig.botService.chat.*;
import com.hedvig.botService.enteties.userContextHelpers.BankAccount;
import com.hedvig.botService.serviceIntegration.memberService.MemberService;
import com.hedvig.botService.serviceIntegration.productPricing.ProductPricingService;
import com.hedvig.botService.web.dto.*;

import com.hedvig.botService.web.dto.events.memberService.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.hedvig.botService.enteties.ConversationEntity;
import com.hedvig.botService.enteties.MemberChat;
import com.hedvig.botService.enteties.MemberChatRepository;
import com.hedvig.botService.enteties.ResourceNotFoundException;
import com.hedvig.botService.enteties.UserContext;
import com.hedvig.botService.enteties.UserContextRepository;
import com.hedvig.botService.enteties.message.Message;
import com.hedvig.botService.chat.Conversation.EventTypes;
import org.springframework.beans.factory.annotation.Value;

public class SessionManager {

    private static Logger log = LoggerFactory.getLogger(SessionManager.class);
    private final MemberChatRepository repo;
    private final UserContextRepository userrepo;
    private final MemberService memberService;
    private final ProductPricingService productPricingclient;

    public enum conversationTypes {MainConversation, OnboardingConversationDevi, UpdateInformationConversation, ClaimsConversation}

	@Autowired
	private OnboardingConversationDevi onboardingConversation;
	@Autowired
	private MainConversation mainConversation;
	@Autowired
	private ClaimsConversation claimsConversation;
	@Autowired
	private UpdateInformationConversation infoConversation;
	
    @Autowired
    public SessionManager(MemberChatRepository repo, UserContextRepository userrepo, MemberService memberService, ProductPricingService client) {
        this.repo = repo;
        this.userrepo = userrepo;
        this.memberService = memberService;
        this.productPricingclient = client;
    }

    public List<Message> getMessages(int i, String hid) {
        log.info("Getting " + i + " messages for user:" + hid);
        List<Message>  messages = getAllMessages(hid);

        return messages.subList(Math.max(messages.size() - i, 0), messages.size());
    }

    public void initClaim(String hid){
    	
        MemberChat mc = repo.findByMemberId(hid).orElseThrow(() -> new ResourceNotFoundException("Could not find memberchat."));
        UserContext uc = userrepo.findByMemberId(hid).orElseThrow(() -> new ResourceNotFoundException("Could not find usercontext."));

		//ClaimsConversation claimsConversation = new ClaimsConversation();
		startConversation(claimsConversation, uc, mc);
		
    	//uc.initClaim();
        userrepo.saveAndFlush(uc);
    }
    
    public void recieveEvent(String eventtype, String value, String hid){
        MemberChat mc = repo.findByMemberId(hid).orElseThrow(() -> new ResourceNotFoundException("Could not find memberchat."));
        UserContext uc = userrepo.findByMemberId(hid).orElseThrow(() -> new ResourceNotFoundException("Could not find usercontext."));
        
        EventTypes type = EventTypes.valueOf(eventtype);

        for(ConversationEntity c : uc.getConversations()){
        	
        	// Only deliver messages to ongoing conversations
        	if(!c.getConversationStatus().equals(Conversation.conversationStatus.ONGOING))continue;
        	
        	switch(c.getClassName()){
				case "com.hedvig.botService.chat.MainConversation":
		        	mainConversation.recieveEvent(type, value, uc, mc);
					break;
				case "com.hedvig.botService.chat.ClaimsConversation":
		            claimsConversation.recieveEvent(type, value, uc, mc);
					break;
				case "com.hedvig.botService.chat.OnboardingConversationDevi":
		        	onboardingConversation.recieveEvent(type, value, uc, mc);
					break;
				case "com.hedvig.botService.chat.UpdateInformationConversation":
		            infoConversation.recieveEvent(type, value, uc, mc);                    
					break;
			}
        }
        
        /*for(conversationTypes name : conversationTypes.values()){
        	if(uc.hasOngoingConversation(name.toString())){
        		Conversation c = null;
        		switch(name){
        		case MainConversation:
                	c = new MainConversation();
        			break;
        		case ClaimsConversation:
                    c = new ClaimsConversation();
        			break;
        		case OnboardingConversationDevi:
                	c = new OnboardingConversationDevi(memberService, this.productPricingclient);
        			break;
        		case UpdateInformationConversation:
                    c = new UpdateInformationConversation();                      
        			break;
        		}
        		c.recieveEvent(type, value, uc, mc);
        	}
        }*/
        
        repo.saveAndFlush(mc);
        userrepo.saveAndFlush(uc);
    }
    
    /*
     * Create a new users chat and context
     * */
    public void init(String hid){
    	
        MemberChat mc = repo.findByMemberId(hid).orElseGet(() -> {
            MemberChat newChat = new MemberChat(hid);
            repo.save(newChat);
            return newChat;
        });

		UserContext uc = userrepo.findByMemberId(hid).orElseGet(() -> {
			UserContext newUserContext = new UserContext(hid);
			userrepo.save(newUserContext);
		    return newUserContext;
		});   	
		
		/*
		 * Kick off onboarding conversation
		 * */
        //OnboardingConversationDevi onboardingConversation = new OnboardingConversationDevi(memberService, this.productPricingclient);
        startConversation(onboardingConversation, uc, mc);
        
        repo.saveAndFlush(mc);
        userrepo.saveAndFlush(uc);
        
    }
    
    /*
     * Mark all messages (incl) last input from user deleted
     * */
    public void editHistory(String hid){
    	MemberChat mc = repo.findByMemberId(hid).orElseThrow(() -> new ResourceNotFoundException("Could not find memberchat."));
    	mc.revertLastInput();
    	repo.saveAndFlush(mc);
    }
    
    /*
     * Start a conversation for a user
     * */
    private void startConversation(Conversation c, UserContext uc, MemberChat mc){
    	log.info("Starting conversation of type:" + c.getClass().getName() + " for user:" + uc.getMemberId());
    	
    	ConversationEntity conv = new ConversationEntity();
    	conv.setClassName(c.getClass().getName());
    	conv.setMemberId(uc.getMemberId());
    	conv.setConversationStatus(Conversation.conversationStatus.ONGOING);
    	c.init(uc, mc);
    	uc.addConversation(conv);
    	
    	//uc.putUserData("{"+ c.getClass().getName() +"}", Conversation.conversationStatus.ONGOING.toString());
    	//c.init(uc, mc);
    }
    
    private void startConversation(Conversation c, UserContext uc, MemberChat mc, String startMessage){
    	log.info("Starting conversation of type:" + c.getClass().getName() + " for user:" + uc.getMemberId());
    	
    	ConversationEntity conv = new ConversationEntity();
    	conv.setClassName(c.getClass().getName());
    	conv.setMemberId(uc.getMemberId());
    	conv.setConversationStatus(Conversation.conversationStatus.INITIATED);
    	conv.setStartMessage(startMessage);
    	c.init(uc, mc);
    	uc.addConversation(conv);
    	
    	//uc.putUserData("{"+ c.getClass().getName() +"}", Conversation.conversationStatus.ONGOING.toString());
    	//c.init(uc, mc);
    }
    
    /*
     * Mark all messages (incl) last input from user deleted
     * */
    public void resetOnboardingChat(String hid){
    	MemberChat mc = repo.findByMemberId(hid).orElseThrow(() -> new ResourceNotFoundException("Could not find memberchat."));
    	UserContext uc = userrepo.findByMemberId(hid).orElseThrow(() -> new ResourceNotFoundException("Could not find usercontext."));
    	
    	mc.reset(); // Clear chat
        //OnboardingConversationDevi onboardingConversation = new OnboardingConversationDevi(memberService, this.productPricingclient);
        startConversation(onboardingConversation, uc, mc);
        
    	repo.saveAndFlush(mc);
    	userrepo.saveAndFlush(uc);
    }
    
    public void setInsuranceStatus(String hid, String status){
    	productPricingclient.setInsuranceStatus(hid, status); 
    }
    
    public List<Message> getAllMessages(String hid) {
        log.info("Getting all messages for user:" + hid);

        /*
         * Find users chat and context. First time it is created
         * */
        MemberChat chat = repo.findByMemberId(hid).orElseGet(() -> {
                    MemberChat newChat = new MemberChat(hid);
                    repo.save(newChat);
                    return newChat;
                });

        UserContext uc = userrepo.findByMemberId(hid).orElseGet(() -> {
        	UserContext newUserContext = new UserContext(hid);
        	userrepo.save(newUserContext);
            return newUserContext;
        });
        
        repo.saveAndFlush(chat);
        userrepo.saveAndFlush(uc);
        
        log.info(chat.toString());
        
        // Mark last user input with as editAllowed
        chat.markLastInput();
        

        // Check for deleted messages
        ArrayList<Message> returnList = new ArrayList<Message>();
        for(Message m : chat.chatHistory){
        	if(m.deleted==null | !m.deleted){ // TODO:remove null test
        		returnList.add(m); 
        	}
        }
        
        /*
         * Sort in global Id order
         * */
    	Collections.sort(returnList, new Comparator<Message>(){
      	     public int compare(Message m1, Message m2){
      	         if(m1.globalId == m2.globalId)
      	             return 0;
      	         return m1.globalId < m2.globalId ? -1 : 1;
      	     }
      	});
    	
    	if(returnList.size() > 0){
	    	Message lastMessage = returnList.get(returnList.size() - 1);
	    	if(lastMessage!=null)recieveEvent("MESSAGE_FETCHED", lastMessage.id, hid);
    	}else{
    		log.info("No messages in chat....");
    	}
        return returnList;
    }
    
    /*
     * Add the "what do you want to do today" message to the chat
     * */
    
    public void mainMenu(String hid){
        log.info("Main menu from user:" + hid);
 
        MemberChat mc = repo.findByMemberId(hid).orElseThrow(() -> new ResourceNotFoundException("Could not find memberchat."));
        UserContext uc = userrepo.findByMemberId(hid).orElseThrow(() -> new ResourceNotFoundException("Could not find usercontext."));

    	/*
    	 * No ongoing main conversation and onboarding complete -> show menu
    	 * */
    	//if(
    			//!uc.hasOngoingConversation(conversationTypes.OnboardingConversationDevi.toString()) && 
    	//		!uc.hasOngoingConversation(conversationTypes.MainConversation.toString())){
    		//MainConversation mainConversation = new MainConversation();
    		startConversation(mainConversation,uc, mc);
    	//}
        /*
         * User is chatting in the main chat:
         * 
        if(!uc.ongoingMainConversation()) {
        	uc.startMainConversation();
            mainConversation.init();
        }*/

        repo.saveAndFlush(mc);
        userrepo.saveAndFlush(uc);    	
    }
    
    /*
     * User wants to update some information
     * */
    public void updateInfo(String hid, UpdateTypes what){
        log.info("Upate info request from user:" + hid);
 
        String startingMessage = "";
        
    	switch(what){
    	case APARTMENT_INFORMATION: startingMessage = "";
    		break;
    	case BANK_ACCOUNT: startingMessage = "";
    		break;
    	case FAMILY_MEMBERS: startingMessage = "";
    		break;
    	case PERSONAL_INFORMATOIN: startingMessage = "";
    		break;
    	case SAFETY_INCREASERS: startingMessage = "";
			break;
    	}
    	
        MemberChat mc = repo.findByMemberId(hid).orElseThrow(() -> new ResourceNotFoundException("Could not find memberchat."));
        UserContext uc = userrepo.findByMemberId(hid).orElseThrow(() -> new ResourceNotFoundException("Could not find usercontext."));
        
        //UpdateInformationConversation conversation = new UpdateInformationConversation();
        //conversation.setStartingMessage(startingMessage);
        startConversation(infoConversation, uc, mc, startingMessage);
        /*uc.putUserData("{"+conversation.getConversationName()+"}", Conversation.conversationStatus.ONGOING.toString());
    	conversation.init();*/

        repo.saveAndFlush(mc);
        userrepo.saveAndFlush(uc);    	
    }
    
    public void receiveEvent(MemberAuthedEvent e){
    	log.info("Received MemberAuthedEvent {}", e.toString());
        String hid = e.getMemberId().toString();
    	UserContext uc = userrepo.findByMemberId(hid).orElseThrow(() -> new ResourceNotFoundException("Could not find usercontext for user:" + hid));

        MemberChat mc = repo.findByMemberId(hid).orElseThrow(() -> new ResourceNotFoundException("Could not find memberchat."));

        if(uc.hasOngoingConversation(conversationTypes.OnboardingConversationDevi.toString())){
            //OnboardingConversationDevi onboardingConversation = new OnboardingConversationDevi(memberService, this.productPricingclient);
            //onboardingConversation.bankIdAuthComplete(e);
            onboardingConversation.bankIdAuthComplete(e, uc, mc);
        }

        repo.saveAndFlush(mc);
        userrepo.saveAndFlush(uc);
    }

    public void receiveEvent(MemberServiceEvent e){
        log.info("Received BankAccountsRetrievedEvent {}", e.toString());

        MemberServiceEventPayload payload = e.getPayload();
        if(payload.getClass() == BankAccountRetrievalSuccess.class) {
            this.handleBankAccountRetrievalSuccess(e, (BankAccountRetrievalSuccess)payload);
        }else if(payload.getClass() == BankAccountRetrievalFailed.class) {
            this.handleBankAccountRetrievalFailed(e, (BankAccountRetrievalFailed)payload);
        }else if(payload.getClass() == MemberSigned.class) {
            this.handleMemberSingedEvent(e, (MemberSigned)payload);
        }

    }

    private void handleMemberSingedEvent(MemberServiceEvent e, MemberSigned payload) {
        String hid = e.getMemberId().toString();
        UserContext uc = userrepo.findByMemberId(hid).orElseThrow(() -> new ResourceNotFoundException("Could not find usercontext for user:" + hid));
        MemberChat mc = repo.findByMemberId(hid).orElseThrow(() -> new ResourceNotFoundException("Could not find memberchat."));

        if(uc.hasOngoingConversation(conversationTypes.OnboardingConversationDevi.toString())){
            //OnboardingConversationDevi onboardingConversation = new OnboardingConversationDevi(memberService, this.productPricingclient);
            onboardingConversation.memberSigned(payload.getReferenceId(), uc, mc);
        }

        repo.saveAndFlush(mc);
        userrepo.saveAndFlush(uc);
    }

    private void handleBankAccountRetrievalFailed(MemberServiceEvent e, BankAccountRetrievalFailed payload) {
        log.info("Handle failed event {}, {}", e, payload);
        String hid = e.getMemberId().toString();
        UserContext uc = userrepo.findByMemberId(hid).orElseThrow(() -> new ResourceNotFoundException("Could not find usercontext for user:" + hid));
        MemberChat mc = repo.findByMemberId(hid).orElseThrow(() -> new ResourceNotFoundException("Could not find memberchat."));

        if(uc.hasOngoingConversation(conversationTypes.OnboardingConversationDevi.toString())){
            //OnboardingConversationDevi onboardingConversation = new OnboardingConversationDevi(memberService, this.productPricingclient);
            onboardingConversation.bankAccountRetrieveFailed(uc, mc);
        }

        repo.saveAndFlush(mc);
        userrepo.saveAndFlush(uc);
    }

    private void handleBankAccountRetrievalSuccess(MemberServiceEvent e, BankAccountRetrievalSuccess payload) {

        String hid = e.getMemberId().toString();
        UserContext uc = userrepo.findByMemberId(hid).orElseThrow(() -> new ResourceNotFoundException("Could not find usercontext for user:" + hid));
        List<BankAccountDetails> details = payload.getAccounts();
        List<BankAccount> accounts = new ArrayList<>();

        for(int i=0; i < details.size(); i++) {

            BankAccountDetails bad = details.get(i);
            BankAccount ba = new BankAccount(bad.getName(), bad.getClearingNumber(), bad.getNumber(), bad.getAmount());
            accounts.add(ba);
        }
        uc.getAutogiroData().setAccounts(accounts);

        MemberChat mc = repo.findByMemberId(hid).orElseThrow(() -> new ResourceNotFoundException("Could not find memberchat."));

        if(uc.hasOngoingConversation(conversationTypes.OnboardingConversationDevi.toString())){
            //OnboardingConversationDevi onboardingConversation = new OnboardingConversationDevi(memberService, this.productPricingclient);
            onboardingConversation.bankAccountRetrieved(uc, mc);
        }

        repo.saveAndFlush(mc);
        userrepo.saveAndFlush(uc);
    }

    public void quoteAccepted(String hid) {
        UserContext uc = userrepo.findByMemberId(hid).orElseThrow(() -> new ResourceNotFoundException("Could not find usercontext for user:" + hid));
        MemberChat mc = repo.findByMemberId(hid).orElseThrow(() -> new ResourceNotFoundException("Could not find memberchat."));

        if(uc.hasOngoingConversation(conversationTypes.OnboardingConversationDevi.toString())){
            //OnboardingConversationDevi onboardingConversation = new OnboardingConversationDevi(memberService, this.productPricingclient);
            onboardingConversation.quoteAccepted(uc, mc);
        }

        repo.save(mc);
        userrepo.save(uc);

    }

    public void receiveMessage(Message m, String hid) {
        log.info("Recieving messages from user:" + hid);
        log.info(m.toString());

        m.header.fromId = new Long(hid);

        MemberChat mc = repo.findByMemberId(hid).orElseThrow(() -> new ResourceNotFoundException("Could not find memberchat."));
        UserContext uc = userrepo.findByMemberId(hid).orElseThrow(() -> new ResourceNotFoundException("Could not find usercontext."));
        
        for(ConversationEntity c : uc.getConversations()){
        	
        	// Only deliver messages to ongoing conversations
        	if(!c.getConversationStatus().equals(Conversation.conversationStatus.ONGOING))continue;
        	
        	switch(c.getClassName()){
				case "com.hedvig.botService.chat.MainConversation":
		        	mainConversation.recieveMessage(uc, mc, m);
					break;
				case "com.hedvig.botService.chat.ClaimsConversation":
		            claimsConversation.recieveMessage(uc, mc, m);
					break;
				case "com.hedvig.botService.chat.OnboardingConversationDevi":
		        	onboardingConversation.recieveMessage(uc, mc, m);
					break;
				case "com.hedvig.botService.chat.UpdateInformationConversation":
		            infoConversation.recieveMessage(uc, mc, m);                     
					break;
			}
        }
        
        
        /*
         * Check all conversations I am involved in
         * */
        /*for(conversationTypes name : conversationTypes.values()){
        	if(uc.hasOngoingConversation(name.toString())){
        		Conversation c = null;
        		switch(name){
        		case MainConversation:
                	c = new MainConversation(this);
        			break;
        		case ClaimsConversation:
                    c = new ClaimsConversation(this);
        			break;
        		case OnboardingConversationDevi:
                	c = new OnboardingConversationDevi(memberService,this, this.productPricingclient);
        			break;
        		case UpdateInformationConversation:
                    c = new UpdateInformationConversation(this);                      
        			break;
        		}
        		if(c==null)throw new RuntimeException("Conversation not found for user :" + hid + " message id:" + m.id);
        		c.recieveMessage(uc, mc, m);
        	}
        }*/

        repo.saveAndFlush(mc);
        userrepo.saveAndFlush(uc);
    }
}
