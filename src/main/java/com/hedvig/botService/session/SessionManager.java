package com.hedvig.botService.session;

import com.hedvig.botService.chat.*;
import com.hedvig.botService.chat.Conversation.EventTypes;
import com.hedvig.botService.enteties.*;
import com.hedvig.botService.enteties.message.Message;
import com.hedvig.botService.enteties.userContextHelpers.BankAccount;
import com.hedvig.botService.serviceIntegration.FakeMemberCreator;
import com.hedvig.botService.serviceIntegration.memberService.MemberService;
import com.hedvig.botService.serviceIntegration.memberService.dto.BankIdCollectResponse;
import com.hedvig.botService.serviceIntegration.productPricing.ProductPricingService;
import com.hedvig.botService.web.dto.MemberAuthedEvent;
import com.hedvig.botService.web.dto.SignupStatus;
import com.hedvig.botService.web.dto.UpdateTypes;
import com.hedvig.botService.web.dto.events.memberService.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/*
 * The session manager is the main controller class for the chat service. It contains all user sessions with chat histories, context etc
 * It is a singleton accessed through the request controller
 * */

@Component
@Transactional
public class SessionManager {

    private static Logger log = LoggerFactory.getLogger(SessionManager.class);
    private final UserContextRepository userrepo;
    private final MemberService memberService;
    private final ProductPricingService productPricingclient;
    private final FakeMemberCreator fakeMemberCreator;
    private final SignupCodeRepository signupRepo;
    private final ApplicationEventPublisher publisher;

    public enum conversationTypes {MainConversation, OnboardingConversationDevi, UpdateInformationConversation, ClaimsConversation}

	
    @Autowired
    public SessionManager(UserContextRepository userrepo, MemberService memberService, ProductPricingService client, FakeMemberCreator fakeMemberCreator, SignupCodeRepository signupRepo, ApplicationEventPublisher publisher) {
        this.userrepo = userrepo;
        this.memberService = memberService;
        this.productPricingclient = client;
        this.fakeMemberCreator = fakeMemberCreator;
        this.signupRepo = signupRepo;
        this.publisher = publisher;
    }

    public List<Message> getMessages(int i, String hid) {
        log.info("Getting " + i + " messages for user:" + hid);
        List<Message>  messages = getAllMessages(hid);

        return messages.subList(Math.max(messages.size() - i, 0), messages.size());
    }

    public SignupStatus getSignupQueuePosition(String externalToken){

        ArrayList<SignupCode> scList = (ArrayList<SignupCode>) signupRepo.findAllByOrderByDateAsc();
        int pos = 1;
        SignupStatus ss = new SignupStatus();
        
        for(SignupCode sc : scList){
        		log.debug(sc.code + " UUID:" + sc.externalToken + " email:" + sc.email + "(" + sc.date+"):" + (pos));
        		if(sc.externalToken.toString().equals(externalToken)){
        			if(!sc.active){
        				ss.position = 90 + pos; // TODO: Remove constant!
        				ss.status = SignupStatus.states.WAITLIST.toString();
        				return ss;
        			}else{
        				ss.code = sc.code;
        				ss.status = SignupStatus.states.ACCESS.toString();
        				return ss;
        			}
        		}
        		if(!sc.used)pos++;
        }
        ss.status = SignupStatus.states.NOT_FOUND.toString();
        return ss;
    }
    
    public void initClaim(String hid){
        UserContext uc = userrepo.findByMemberId(hid).orElseThrow(() -> new ResourceNotFoundException("Could not find usercontext."));
        ClaimsConversation claimsConversation = new ClaimsConversation(memberService, productPricingclient);
        uc.startConversation(claimsConversation);

        userrepo.saveAndFlush(uc);
    }

    public void initClaim(String hid, String assetId) {
        UserContext uc = userrepo.findByMemberId(hid).orElseThrow(() -> new ResourceNotFoundException("Could not find usercontext for user:" + hid));
        ClaimsConversation claimsConversation = new ClaimsConversation(memberService, productPricingclient);
        uc.startConversation(claimsConversation, "init.asset.claim");

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
				    OnboardingConversationDevi onboardingConversation = createOnboaringConversation();
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

    public BankIdCollectResponse collect(String hid, String referenceToken) {

        CollectService service = new CollectService(userrepo, memberService);

        return service.collect(hid, referenceToken, createOnboaringConversation());
    }

    private OnboardingConversationDevi createOnboaringConversation() {
        return new OnboardingConversationDevi(memberService, productPricingclient, fakeMemberCreator, signupRepo, publisher);
    }

    /*
     * Kicks off onboarding conversation with either direct login or regular signup flow
     * */
    public void startOnboardingConversation(String hid, String startMsg){
    	
    	UserContext uc = userrepo.findByMemberId(hid).orElseThrow(() -> new ResourceNotFoundException("Could not find usercontext."));
    	uc.putUserData("{WEB_USER}", "FALSE");
    	
        OnboardingConversationDevi onboardingConversation = createOnboaringConversation();
        uc.startConversation(onboardingConversation, startMsg);

        userrepo.saveAndFlush(uc);
    }
    
    /*
     * Kicks off onboarding web conversation
     * */
    public void startOnboardingConversationWeb(String hid, String startMsg){
    	
    	UserContext uc = userrepo.findByMemberId(hid).orElseThrow(() -> new ResourceNotFoundException("Could not find usercontext."));
    	
    	// TODO: Make sure it is only possible to activate this endpoint ones
    	if(uc.getDataEntry("{WEB_USER}") == null){
	    	uc.putUserData("{WEB_USER}", "TRUE");
	    	
	        OnboardingConversationDevi onboardingConversation = createOnboaringConversation();
	        uc.startConversation(onboardingConversation, startMsg);
	
	        userrepo.saveAndFlush(uc);
    	}
    }    
    
    /*
     * Create a new users chat and context
     * */
    public void init(String hid, String linkUri){

        UserContext uc = userrepo.findByMemberId(hid).orElseGet(() -> {
            UserContext newUserContext = new UserContext(hid);
            MemberChat newChat = new MemberChat(hid);
            newChat.userContext = newUserContext;
            newUserContext.setMemberChat(newChat);
            userrepo.save(newUserContext);

            return newUserContext;
        });

        uc.putUserData("{LINK_URI}", linkUri);


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
    
    public void addMessageFromHedvig(Message m, String hid){
    	
        UserContext uc = userrepo.findByMemberId(hid).orElseThrow(() -> new ResourceNotFoundException("Could not find usercontext."));
    	MemberChat mc = uc.getMemberChat();
    	mc.addToHistory(m);
    	userrepo.saveAndFlush(uc);
    	
    }
    
    /*
     * Mark all messages (incl) last input from user deleted
     * */
    public void resetOnboardingChat(String hid){
    	UserContext uc = userrepo.findByMemberId(hid).orElseThrow(() -> new ResourceNotFoundException("Could not find usercontext."));
        MemberChat mc = uc.getMemberChat();
        
        // Conversations can only be reset during onboarding
        if(!uc.hasCompletedOnboarding()){
        	
        	String email = uc.getOnBoardingData().getEmail(); // TODO: remove hack
	    	mc.reset(); // Clear chat
	    	uc.clearContext(); // Clear context
	    	
	    	uc.getOnBoardingData().setEmail(email); // TODO: remove hack
	    	
	        OnboardingConversationDevi onboardingConversation = createOnboaringConversation();
	        uc.startConversation(onboardingConversation);
	    	userrepo.saveAndFlush(uc);
        }
    }
    
    public void setInsuranceStatus(String hid, String status){
    	productPricingclient.setInsuranceStatus(hid, status); 
    }
    
    public List<Message> getAllMessages(String hid) {

        /*
         * Find users chat and context. First time it is created
         * */

        UserContext uc = userrepo.findByMemberId(hid).orElseThrow(() -> new ResourceNotFoundException("Could not find usercontext."));

        MemberChat chat = uc.getMemberChat();

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
	    	if(lastMessage!=null) {
                recieveEvent("MESSAGE_FETCHED", lastMessage.id, hid);
            }
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

        MainConversation mainConversation = new MainConversation(memberService, productPricingclient);
        uc.startConversation(mainConversation);

        userrepo.saveAndFlush(uc);    	
    }
    
    /*
     * User wants to update some information
     * */
    public void updateInfo(String hid, UpdateTypes what){
        log.info("Upate info request from user:" + hid);
 
        String startingMessage = "";
        
    	switch(what){
    	case APARTMENT_INFORMATION: startingMessage = "message.info.update";
    		break;
    	case BANK_ACCOUNT: startingMessage = "message.info.update.payment";
    		break;
    	case FAMILY_MEMBERS: startingMessage = "message.info.update";
    		break;
    	case PERSONAL_INFORMATOIN: startingMessage = "message.info.update";
    		break;
    	case SAFETY_INCREASERS: startingMessage = "message.info.update.safety";
			break;
    	}
    	
        UserContext uc = userrepo.findByMemberId(hid).orElseThrow(() -> new ResourceNotFoundException("Could not find usercontext."));
        UpdateInformationConversation infoConversation = new UpdateInformationConversation(memberService, productPricingclient);

        uc.startConversation(infoConversation, startingMessage);
        userrepo.saveAndFlush(uc);
    }
    
    public void receiveEvent(MemberAuthedEvent e){
    	log.warn("Received unwanted MemberAuthedEvent {}", e.toString());
    }

    public void receiveEvent(MemberServiceEvent e){
        log.info("Received BankAccountsRetrievedEvent {}", e.toString());

        MemberServiceEventPayload payload = e.getPayload();
        if(payload.getClass() == BankAccountRetrievalSuccess.class) {
            this.handleBankAccountRetrievalSuccess(e, (BankAccountRetrievalSuccess)payload);
        }else if(payload.getClass() == BankAccountRetrievalFailed.class) {
            this.handleBankAccountRetrievalFailed(e, (BankAccountRetrievalFailed)payload);
        }else {
            log.warn("Recieved unhandled event e:{}", e);
        }

    }

    private void handleBankAccountRetrievalFailed(MemberServiceEvent e, BankAccountRetrievalFailed payload) {
        log.info("Handle failed event {}, {}", e, payload);
        String hid = e.getMemberId().toString();
        UserContext uc = userrepo.findByMemberId(hid).orElseThrow(() -> new ResourceNotFoundException("Could not find usercontext for user:" + hid));
        MemberChat mc = uc.getMemberChat();

        //if(uc.hasOngoingConversation(conversationTypes.OnboardingConversationDevi.toString())){
            OnboardingConversationDevi onboardingConversation = createOnboaringConversation();
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
            OnboardingConversationDevi onboardingConversation = createOnboaringConversation();
            onboardingConversation.bankAccountRetrieved(uc, mc);
//        }

        userrepo.saveAndFlush(uc);
    }

    public void quoteAccepted(String hid) {
        UserContext uc = userrepo.findByMemberId(hid).orElseThrow(() -> new ResourceNotFoundException("Could not find usercontext for user:" + hid));
        MemberChat mc = uc.getMemberChat();

//        if(uc.hasOngoingConversation(conversationTypes.OnboardingConversationDevi.toString())){
            OnboardingConversationDevi onboardingConversation = createOnboaringConversation();
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
				    OnboardingConversationDevi onboardingConversation = createOnboaringConversation();
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

	public Integer getWaitlistPosition(String email) {
		// TODO Auto-generated method stub
		return null;
	}


}
