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
import com.hedvig.botService.enteties.userContextHelpers.UserData;
import com.hedvig.botService.serviceIntegration.MemberService;
import com.hedvig.botService.serviceIntegration.productPricing.ProductPricingService;
import com.hedvig.botService.web.dto.*;

import com.hedvig.botService.web.dto.events.memberService.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.hedvig.botService.enteties.MemberChat;
import com.hedvig.botService.enteties.MemberChatRepository;
import com.hedvig.botService.enteties.Message;
import com.hedvig.botService.enteties.ResourceNotFoundException;
import com.hedvig.botService.enteties.UserContext;
import com.hedvig.botService.enteties.UserContextRepository;
import com.hedvig.botService.serviceIntegration.productPricing.ProductPricingClient;
import com.hedvig.botService.chat.Conversation.EventTypes;
import org.springframework.beans.factory.annotation.Value;

public class SessionManager {

    private static Logger log = LoggerFactory.getLogger(SessionManager.class);
    private final MemberChatRepository repo;
    private final UserContextRepository userrepo;
    private final MemberService memberService;
    private final ProductPricingService productPricingclient;

    public enum conversationTypes {MainConversation, OnboardingConversationDevi, UpdateInformationConversation, ClaimsConversation}

    @Value("${hedvig.gateway.url:http://gateway.hedvig.com}")
    public String gatewayUrl;
    
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

		ClaimsConversation claimsConversation = new ClaimsConversation(mc, uc);
		startConversation(claimsConversation, uc);
		
    	uc.initClaim();
        userrepo.saveAndFlush(uc);
    }
    
    public void recieveEvent(String eventtype, String value, String hid){
        MemberChat mc = repo.findByMemberId(hid).orElseThrow(() -> new ResourceNotFoundException("Could not find memberchat."));
        UserContext uc = userrepo.findByMemberId(hid).orElseThrow(() -> new ResourceNotFoundException("Could not find usercontext."));
        
        EventTypes type = EventTypes.valueOf(eventtype);

        for(conversationTypes name : conversationTypes.values()){
        	if(uc.hasOngoingConversation(name.toString())){
        		Conversation c = null;
        		switch(name){
        		case MainConversation:
                	c = new MainConversation(mc, uc);
        			break;
        		case ClaimsConversation:
                    c = new ClaimsConversation(mc, uc);
        			break;
        		case OnboardingConversationDevi:
                	c = new OnboardingConversationDevi(mc, uc, memberService, this.productPricingclient, gatewayUrl);
        			break;
        		case UpdateInformationConversation:
                    c = new UpdateInformationConversation(mc, uc);                      
        			break;
        		}
        		c.recieveEvent(type, value);
        	}
        }
        
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
        OnboardingConversationDevi onboardingConversation = new OnboardingConversationDevi(mc, uc, memberService, this.productPricingclient, gatewayUrl);
        startConversation(onboardingConversation, uc);
        
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
    private void startConversation(Conversation c, UserContext uc){
    	log.info("Starting conversation of type:" + c.getClass().getName() + " for user:" + uc.getMemberId());
    	uc.putUserData("{"+ c.getClass().getName() +"}", Conversation.conversationStatus.ONGOING.toString());
    	c.init();
    }
    
    /*
     * Mark all messages (incl) last input from user deleted
     * */
    public void resetOnboardingChat(String hid){
    	MemberChat mc = repo.findByMemberId(hid).orElseThrow(() -> new ResourceNotFoundException("Could not find memberchat."));
    	UserContext uc = userrepo.findByMemberId(hid).orElseThrow(() -> new ResourceNotFoundException("Could not find usercontext."));
    	
    	mc.reset(); // Clear chat
        OnboardingConversationDevi onboardingConversation = new OnboardingConversationDevi(mc, uc, memberService, this.productPricingclient, gatewayUrl);
        startConversation(onboardingConversation, uc);
        
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
    		MainConversation mainConversation = new MainConversation(mc, uc);
    		startConversation(mainConversation,uc);
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
        
        UpdateInformationConversation conversation = new UpdateInformationConversation(mc, uc, startingMessage);
        startConversation(conversation, uc);
        /*uc.putUserData("{"+conversation.getConversationName()+"}", Conversation.conversationStatus.ONGOING.toString());
    	conversation.init();*/

        repo.saveAndFlush(mc);
        userrepo.saveAndFlush(uc);    	
    }
    
    public void receiveEvent(MemberAuthedEvent e){
    	log.info("Received MemberAuthedEvent {}", e.toString());
        String hid = e.getMemberId().toString();
    	UserContext uc = userrepo.findByMemberId(hid).orElseThrow(() -> new ResourceNotFoundException("Could not find usercontext for user:" + hid));
        Member member = e.getMember();

        UserData obd = uc.getOnBoardingData();
        obd.setBirthDate(member.getBirthDate());
        obd.setSSN(member.getSsn());
        obd.setFirstName(member.getFirstName());
        obd.setFamilyName(member.getLastName());

    	obd.setEmail(member.getEmail());

    	obd.setAddressStreet(member.getStreet());
    	obd.setAddressCity(member.getCity());
    	obd.setAddressZipCode(member.getZipCode());

        MemberChat mc = repo.findByMemberId(hid).orElseThrow(() -> new ResourceNotFoundException("Could not find memberchat."));

        if(uc.hasOngoingConversation(conversationTypes.OnboardingConversationDevi.toString())){
            OnboardingConversationDevi onboardingConversation = new OnboardingConversationDevi(mc, uc, memberService, this.productPricingclient, gatewayUrl);
            onboardingConversation.bankIdAuthComplete();
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
            OnboardingConversationDevi onboardingConversation = new OnboardingConversationDevi(mc, uc, memberService, this.productPricingclient, gatewayUrl);
            onboardingConversation.memberSigned(payload.getReferenceId());
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
            OnboardingConversationDevi onboardingConversation = new OnboardingConversationDevi(mc, uc, memberService, this.productPricingclient, gatewayUrl);
            onboardingConversation.bankAccountRetrieveFailed();
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
            OnboardingConversationDevi onboardingConversation = new OnboardingConversationDevi(mc, uc, memberService, this.productPricingclient, gatewayUrl);
            onboardingConversation.bankAccountRetrieved();
        }

        repo.saveAndFlush(mc);
        userrepo.saveAndFlush(uc);
    }

    public void quoteAccepted(String hid) {
        UserContext uc = userrepo.findByMemberId(hid).orElseThrow(() -> new ResourceNotFoundException("Could not find usercontext for user:" + hid));
        MemberChat mc = repo.findByMemberId(hid).orElseThrow(() -> new ResourceNotFoundException("Could not find memberchat."));

        if(uc.hasOngoingConversation(conversationTypes.OnboardingConversationDevi.toString())){
            OnboardingConversationDevi onboardingConversation = new OnboardingConversationDevi(mc, uc, memberService, this.productPricingclient, gatewayUrl);
            onboardingConversation.quoteAccepted();
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
        
        /*
         * Check all conversations I am involved in
         * */
        for(conversationTypes name : conversationTypes.values()){
        	if(uc.hasOngoingConversation(name.toString())){
        		Conversation c = null;
        		switch(name){
        		case MainConversation:
                	c = new MainConversation(mc, uc);
        			break;
        		case ClaimsConversation:
                    c = new ClaimsConversation(mc, uc);
        			break;
        		case OnboardingConversationDevi:
                	c = new OnboardingConversationDevi(mc, uc, memberService, this.productPricingclient, gatewayUrl);
        			break;
        		case UpdateInformationConversation:
                    c = new UpdateInformationConversation(mc, uc);                      
        			break;
        		}
        		if(c==null)throw new RuntimeException("Conversation not found for user :" + hid + " message id:" + m.id);
        		c.recieveMessage(m);
        	}
        }

        repo.saveAndFlush(mc);
        userrepo.saveAndFlush(uc);
    }
}
