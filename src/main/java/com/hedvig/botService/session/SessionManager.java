package com.hedvig.botService.session;

import com.hedvig.botService.chat.*;
import com.hedvig.botService.chat.Conversation.EventTypes;
import com.hedvig.botService.enteties.*;
import com.hedvig.botService.enteties.message.Message;
import com.hedvig.botService.enteties.userContextHelpers.BankAccount;
import com.hedvig.botService.enteties.userContextHelpers.UserData;
import com.hedvig.botService.serviceIntegration.memberService.MemberService;
import com.hedvig.botService.serviceIntegration.memberService.dto.BankIdAuthResponse;
import com.hedvig.botService.serviceIntegration.memberService.dto.BankIdStatusType;
import com.hedvig.botService.serviceIntegration.productPricing.ProductPricingService;
import com.hedvig.botService.web.dto.Member;
import com.hedvig.botService.web.dto.MemberAuthedEvent;
import com.hedvig.botService.web.dto.UpdateTypes;
import com.hedvig.botService.web.dto.events.memberService.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import java.util.*;

/*
 * The session manager is the main controller class for the chat service. It contains all user sessions with chat histories, context etc
 * It is a singleton accessed through the request controller
 * */

@Component
public class SessionManager {

    private static Logger log = LoggerFactory.getLogger(SessionManager.class);
    private final UserContextRepository userrepo;
    private final MemberService memberService;
    private final ProductPricingService productPricingclient;

    public enum conversationTypes {MainConversation, OnboardingConversationDevi, UpdateInformationConversation, ClaimsConversation}

	
    @Autowired
    public SessionManager(UserContextRepository userrepo, MemberService memberService, ProductPricingService client) {
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
        UserContext uc = userrepo.findByMemberId(hid).orElseThrow(() -> new ResourceNotFoundException("Could not find usercontext."));
        ClaimsConversation claimsConversation = new ClaimsConversation(memberService, productPricingclient);
        uc.startConversation(claimsConversation);

        userrepo.saveAndFlush(uc);
    }
    
    public void recieveEvent(String eventtype, String value, String hid){

        UserContext uc = userrepo.findByMemberId(hid).orElseThrow(() -> new ResourceNotFoundException("Could not find usercontext."));
        MemberChat mc = uc.getMemberChat();

        EventTypes type = EventTypes.valueOf(eventtype);

        for(ConversationEntity c : uc.getConversations()){
        	
        	// Only deliver messages to ongoing conversations
        	if(!c.getConversationStatus().equals(Conversation.conversationStatus.ONGOING))continue;
        	
        	switch(c.getClassName()){
				case "com.hedvig.botService.chat.MainConversation":
				    MainConversation mainConversation = new MainConversation(memberService, productPricingclient);
		        	mainConversation.recieveEvent(type, value, uc, mc);
					break;
				case "com.hedvig.botService.chat.ClaimsConversation":
				    ClaimsConversation claimsConversation = new ClaimsConversation(memberService, productPricingclient);
		            claimsConversation.recieveEvent(type, value, uc, mc);
					break;
				case "com.hedvig.botService.chat.OnboardingConversationDevi":
				    OnboardingConversationDevi onboardingConversation = new OnboardingConversationDevi(memberService, productPricingclient);
		        	onboardingConversation.recieveEvent(type, value, uc, mc);
					break;
				case "com.hedvig.botService.chat.UpdateInformationConversation":
				    UpdateInformationConversation infoConversation = new UpdateInformationConversation(memberService, productPricingclient);
		            infoConversation.recieveEvent(type, value, uc, mc);                    
					break;
			}
        }

        userrepo.saveAndFlush(uc);
    }

    public Optional<BankIdAuthResponse> collect(String hid, String referenceToken) {

        UserContext uc = userrepo.findByMemberId(hid).orElseThrow(() -> new ResourceNotFoundException("Could not find usercontext."));

        OnboardingConversationDevi onboardingConversation = new OnboardingConversationDevi(memberService, productPricingclient);
        try {
            BankIdAuthResponse collect = memberService.collect(referenceToken, hid);
            if(collect.getNewMemberId() != null && !collect.getNewMemberId().equals(hid)){
                uc = userrepo.findByMemberId(collect.getNewMemberId()).orElseThrow(() -> new ResourceNotFoundException("Could not find usercontext."));
            }

            CollectionStatus collectionStatus = uc.getBankIdCollectStatus(referenceToken);
            if(collectionStatus == null) {
                //onboardingConversation.bankIdAuthError(uc);

                return Optional.of(collect);
            }


            BankIdStatusType bankIdStatus = collect.getBankIdStatus();
            log.info("BankIdStatus after collect:{}, memberId:{}, lastCollectionStatus: {}", bankIdStatus.name(), hid, collectionStatus.getLastStatus());
            if (!collectionStatus.done()) {

                if (bankIdStatus == BankIdStatusType.COMPLETE) {
                    //Fetch member data from member service.
                    //Try three times
                    Member member = memberService.getProfile(collect.getNewMemberId());

                    UserData obd = uc.getOnBoardingData();
                    obd.setBirthDate(member.getBirthDate());
                    obd.setSSN(member.getSsn());
                    obd.setFirstName(member.getFirstName());
                    obd.setFamilyName(member.getLastName());

                    //obd.setEmail(member.getEmail()); I don't think we will ever get his from bisnode

                    obd.setAddressStreet(member.getStreet());
                    obd.setAddressCity(member.getCity());
                    obd.setAddressZipCode(member.getZipCode());

                    uc.getOnBoardingData().setUserHasAuthWithBankId(referenceToken);

                    onboardingConversation.bankIdAuthComplete(uc);

                } //else if (bankIdStatus == BankIdStatusType.ERROR) {
                    //Handle error
//                    onboardingConversation.bankIdAuthError(uc);
                //}

                collectionStatus.update(bankIdStatus);
            }//else{
            //    onboardingConversation
            //}

            userrepo.saveAndFlush(uc);

            return Optional.of(collect);
        }catch( HttpClientErrorException ex) {
            log.error("Error collecting result from member-service: ", ex);
            onboardingConversation.bankIdAuthError(uc);
            //Have hedvig respond with error
        }
        return Optional.empty();
    }
    
    /*
     * Create a new users chat and context
     * */
    public void init(String hid){

        UserContext uc = userrepo.findByMemberId(hid).orElseGet(() -> {
            UserContext newUserContext = new UserContext(hid);
            MemberChat newChat = new MemberChat(hid);
            newChat.userContext = newUserContext;
            newUserContext.setMemberChat(newChat);
            userrepo.save(newUserContext);

            return newUserContext;
        });

		/*
		 * Kick off onboarding conversation
		 * */
        OnboardingConversationDevi onboardingConversation = new OnboardingConversationDevi(memberService, this.productPricingclient);
        uc.startConversation(onboardingConversation);

        userrepo.saveAndFlush(uc);
        
    }
    
    /*
     * Mark all messages (incl) last input from user deleted
     * */
    public void editHistory(String hid){
        UserContext uc = userrepo.findByMemberId(hid).orElseThrow(() -> new ResourceNotFoundException("Could not find usercontext."));
    	MemberChat mc = uc.getMemberChat();
    	mc.revertLastInput();
    	userrepo.saveAndFlush(uc);
    }
    
    /*
     * Start a conversation for a user
     * 
    private void startConversation(Conversation c, UserContext uc, MemberChat mc){
    	log.info("Starting conversation of type:" + c.getClass().getName() + " for user:" + uc.getMemberId());
    	
    	ConversationEntity conv = new ConversationEntity();
    	conv.setClassName(c.getClass().getName());
    	conv.setMemberId(uc.getMemberId());
    	conv.setConversationStatus(Conversation.conversationStatus.ONGOING);
    	c.init(uc, mc);
    	uc.addConversation(conv);

    }
    
    private void startConversation(Conversation c, UserContext uc, MemberChat mc, String startMessage){
    	log.info("Starting conversation of type:" + c.getClass().getName() + " for user:" + uc.getMemberId());
    	
    	ConversationEntity conv = new ConversationEntity();
    	conv.setClassName(c.getClass().getName());
    	conv.setMemberId(uc.getMemberId());
    	conv.setConversationStatus(Conversation.conversationStatus.ONGOING);
    	conv.setStartMessage(startMessage);
    	c.init(uc, mc);
    	uc.addConversation(conv);
    	
    	//uc.putUserData("{"+ c.getClass().getName() +"}", Conversation.conversationStatus.ONGOING.toString());
    	//c.init(uc, mc);
    }*/
    
    /*
     * Mark all messages (incl) last input from user deleted
     * */
    public void resetOnboardingChat(String hid){
    	UserContext uc = userrepo.findByMemberId(hid).orElseThrow(() -> new ResourceNotFoundException("Could not find usercontext."));
        MemberChat mc = uc.getMemberChat();
    	mc.reset(); // Clear chat
    	uc.clearContext(); // Clear context
        OnboardingConversationDevi onboardingConversation = new OnboardingConversationDevi(memberService, this.productPricingclient);
        uc.startConversation(onboardingConversation);
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

        UserContext uc = userrepo.findByMemberId(hid).orElseGet(() -> {
        	UserContext newUserContext = new UserContext(hid);
        	userrepo.saveAndFlush(newUserContext);
            return newUserContext;
        });

        log.info(uc.getMemberChat().toString());

        // Mark last user input with as editAllowed
        uc.getMemberChat().markLastInput();

        MemberChat chat = uc.getMemberChat();

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

        userrepo.saveAndFlush(uc);

        return returnList;
    }
    
    /*
     * Add the "what do you want to do today" message to the chat
     * */
    
    public void mainMenu(String hid){
        log.info("Main menu from user:" + hid);
 
        UserContext uc = userrepo.findByMemberId(hid).orElseThrow(() -> new ResourceNotFoundException("Could not find usercontext."));

    	/*
    	 * No ongoing main conversation and onboarding complete -> show menu
    	 * */
    	//if(
    			//!uc.hasOngoingConversation(conversationTypes.OnboardingConversationDevi.toString()) && 
    	//		!uc.hasOngoingConversation(conversationTypes.MainConversation.toString())){
        
    		MainConversation mainConversation = new MainConversation(memberService, productPricingclient);
    		uc.startConversation(mainConversation);
    	//}
        /*
         * User is chatting in the main chat:
         * 
        if(!uc.ongoingMainConversation()) {
        	uc.startMainConversation();
            mainConversation.init();
        }*/

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
    	
        UserContext uc = userrepo.findByMemberId(hid).orElseThrow(() -> new ResourceNotFoundException("Could not find usercontext."));
        UpdateInformationConversation infoConversation = new UpdateInformationConversation(memberService, productPricingclient);

        //conversation.setStartingMessage(startingMessage);
        uc.startConversation(infoConversation, startingMessage);
        /*uc.putUserData("{"+conversation.getConversationName()+"}", Conversation.conversationStatus.ONGOING.toString());
    	conversation.init();*/

        userrepo.saveAndFlush(uc);
    }
    
    public void receiveEvent(MemberAuthedEvent e){
    	log.info("Received MemberAuthedEvent {}", e.toString());
        //String hid = e.getMemberId().toString();
    	//UserContext uc = userrepo.findByMemberId(hid).orElseThrow(() -> new ResourceNotFoundException("Could not find usercontext for user:" + hid));
        //MemberChat mc = uc.getMemberChat();

        //if(uc.hasOngoingConversation(conversationTypes.OnboardingConversationDevi.toString())){
            //onboardingConversation.bankIdAuthComplete(e, uc, mc);
        //}

        //userrepo.saveAndFlush(uc);
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
        MemberChat mc = uc.getMemberChat();

        //if(uc.hasOngoingConversation(conversationTypes.OnboardingConversationDevi.toString())){
            OnboardingConversationDevi onboardingConversation = new OnboardingConversationDevi(memberService, this.productPricingclient);
            onboardingConversation.memberSigned(payload.getReferenceId(), uc, mc);
        //}

        userrepo.saveAndFlush(uc);
    }

    private void handleBankAccountRetrievalFailed(MemberServiceEvent e, BankAccountRetrievalFailed payload) {
        log.info("Handle failed event {}, {}", e, payload);
        String hid = e.getMemberId().toString();
        UserContext uc = userrepo.findByMemberId(hid).orElseThrow(() -> new ResourceNotFoundException("Could not find usercontext for user:" + hid));
        MemberChat mc = uc.getMemberChat();

        //if(uc.hasOngoingConversation(conversationTypes.OnboardingConversationDevi.toString())){
            OnboardingConversationDevi onboardingConversation = new OnboardingConversationDevi(memberService, this.productPricingclient);
            onboardingConversation.bankAccountRetrieveFailed(uc, mc);
        //}

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

        MemberChat mc = uc.getMemberChat();

//        if(uc.hasOngoingConversation(conversationTypes.OnboardingConversationDevi.toString())){
            OnboardingConversationDevi onboardingConversation = new OnboardingConversationDevi(memberService, this.productPricingclient);
            onboardingConversation.bankAccountRetrieved(uc, mc);
//        }

        userrepo.saveAndFlush(uc);
    }

    public void quoteAccepted(String hid) {
        UserContext uc = userrepo.findByMemberId(hid).orElseThrow(() -> new ResourceNotFoundException("Could not find usercontext for user:" + hid));
        MemberChat mc = uc.getMemberChat();

//        if(uc.hasOngoingConversation(conversationTypes.OnboardingConversationDevi.toString())){
            OnboardingConversationDevi onboardingConversation = new OnboardingConversationDevi(memberService, this.productPricingclient);
            onboardingConversation.quoteAccepted(uc, mc);
//        }

        userrepo.save(uc);

    }

    public void receiveMessage(Message m, String hid) {
        log.info("Recieving messages from user:" + hid);
        log.info(m.toString());

        m.header.fromId = new Long(hid);

        UserContext uc = userrepo.findByMemberId(hid).orElseThrow(() -> new ResourceNotFoundException("Could not find usercontext."));
        MemberChat mc = uc.getMemberChat();

        List<ConversationEntity> conversations = new ArrayList<>(uc.getConversations()); //We will add a new element to uc.conversationManager
        for(ConversationEntity c : conversations){
        	
        	// Only deliver messages to ongoing conversations
        	if(!c.getConversationStatus().equals(Conversation.conversationStatus.ONGOING))continue;
        	
        	switch(c.getClassName()){
				case "com.hedvig.botService.chat.MainConversation":
				    MainConversation mainConversation = new MainConversation(memberService, productPricingclient);
		        	mainConversation.recieveMessage(uc, mc, m);
					break;
				case "com.hedvig.botService.chat.ClaimsConversation":
				    ClaimsConversation claimsConversation = new ClaimsConversation(memberService, productPricingclient);
		            claimsConversation.recieveMessage(uc, mc, m);
					break;
				case "com.hedvig.botService.chat.OnboardingConversationDevi":
				    OnboardingConversationDevi onboardingConversation = new OnboardingConversationDevi(memberService, productPricingclient);
		        	onboardingConversation.recieveMessage(uc, mc, m);
					break;
				case "com.hedvig.botService.chat.UpdateInformationConversation":
				    UpdateInformationConversation infoConversation = new UpdateInformationConversation(memberService, productPricingclient);
		            infoConversation.recieveMessage(uc, mc, m);                     
					break;
			}
        }

        userrepo.saveAndFlush(uc);
    }
}
