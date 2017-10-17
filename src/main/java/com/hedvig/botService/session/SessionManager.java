package com.hedvig.botService.session;

/*
 * The session manager is the main controller class for the chat service. It contains all user sessions with chat histories, context etc
 * It is a singleton accessed through the request controller
 * */

import java.util.List;

import com.hedvig.botService.serviceIntegration.AuthService;
import com.hedvig.botService.web.dto.MemberAuthedEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.hedvig.botService.chat.ClaimsConversation;
import com.hedvig.botService.chat.MainConversation;
import com.hedvig.botService.chat.OnboardingConversation;
import com.hedvig.botService.chat.OnboardingConversationDevi;
import com.hedvig.botService.enteties.MemberChat;
import com.hedvig.botService.enteties.MemberChatRepository;
import com.hedvig.botService.enteties.Message;
import com.hedvig.botService.enteties.ResourceNotFoundException;
import com.hedvig.botService.enteties.UserContext;
import com.hedvig.botService.enteties.UserContextRepository;
import com.hedvig.botService.chat.Conversation;
import com.hedvig.botService.chat.Conversation.EventTypes;

public class SessionManager {

    private static Logger log = LoggerFactory.getLogger(SessionManager.class);
    private final MemberChatRepository repo;
    private final UserContextRepository userrepo;
    private final AuthService authService;

    @Autowired
    public SessionManager(MemberChatRepository repo, UserContextRepository userrepo, AuthService authService) {
        this.repo = repo;
        this.userrepo = userrepo;
        this.authService = authService;
    }

    public List<Message> getMessages(int i, String hid) {
        log.info("Getting " + i + " messages for user:" + hid);
        List<Message>  messages = getAllMessages(hid);

        return messages.subList(Math.max(messages.size() - i, 0), messages.size());
    }

    public void initClaim(String hid){
    	
        UserContext uc = userrepo.findByMemberId(hid).orElseGet(() -> {
        	UserContext newUserContext = new UserContext(hid);
        	userrepo.save(newUserContext);
            return newUserContext;
        });
    	uc.initClaim();
        userrepo.saveAndFlush(uc);
    }
    
    public void recieveEvent(String eventtype, String value, String hid){
        MemberChat mc = repo.findByMemberId(hid).orElseThrow(() -> new ResourceNotFoundException("Could not find memberchat."));
        UserContext uc = userrepo.findByMemberId(hid).orElseThrow(() -> new ResourceNotFoundException("Could not find usercontext."));
        
        EventTypes type = EventTypes.valueOf(eventtype);

        /*
         * User is onboaring:
         * */
        if(!uc.onboardingComplete()) {
            //OnboardingConversation onboardingConversation = new OnboardingConversation(mc, uc);
        	OnboardingConversationDevi onboardingConversation = new OnboardingConversationDevi(mc, uc, authService);
            onboardingConversation.recieveEvent(type, value);
        }
       
        /*
         * User is in a claims process:
         * */
        if(uc.ongoingClaimsProcess()){
            ClaimsConversation claimsConversation = new ClaimsConversation(mc, uc);
            claimsConversation.recieveEvent(type, value);       	
        }
        
        /*
         * User is chatting with Hedvig:
         * */
        if(uc.ongoingMainConversation()) {
        	MainConversation mainConversation = new MainConversation(mc, uc);
            mainConversation.recieveEvent(type, value);
        }
    	
        repo.saveAndFlush(mc);
        userrepo.saveAndFlush(uc);
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

        // Still onboarding
        if(!uc.onboardingComplete()) {
	        //OnboardingConversation onboardingConversation = new OnboardingConversation(chat, uc);
	        OnboardingConversationDevi onboardingConversation = new OnboardingConversationDevi(chat, uc, authService);
	        
	        // If this is the first message the Onboarding conversation is initiated
	        if(!uc.onboardingStarted()){
	        	uc.onboardingStarted(true);
	        	onboardingConversation.init();
	        }
        }
        
        	// New claims process
        if(uc.claimsProcessInitiated()){
        		uc.claimStarted();
        		ClaimsConversation claimsConversation = new ClaimsConversation(chat, uc);
        		claimsConversation.init(hid);
        }
        
        repo.saveAndFlush(chat);
        userrepo.saveAndFlush(uc);
        
        log.info(chat.toString());
        return chat.chatHistory;
    }
    
    /*
     * Add the "what do you want to do today" message to the chat
     * */
    
    public void mainMenu(String hid){
        log.info("Main menu from user:" + hid);
 
        MemberChat mc = repo.findByMemberId(hid).orElseThrow(() -> new ResourceNotFoundException("Could not find memberchat."));
        UserContext uc = userrepo.findByMemberId(hid).orElseThrow(() -> new ResourceNotFoundException("Could not find usercontext."));
        
    	MainConversation mainConversation = new MainConversation(mc, uc);
    	
        /*
         * User is chatting in the main chat:
         * */
        if(!uc.ongoingMainConversation()) {
        	uc.startMainConversation();
            mainConversation.init();
        }

        repo.saveAndFlush(mc);
        userrepo.saveAndFlush(uc);    	
    }
    
    public void receiveEvent(MemberAuthedEvent e){
    	String hid = e.getMemberId().toString();
    	UserContext uc = userrepo.findByMemberId(hid).orElseThrow(() -> new ResourceNotFoundException("Could not find usercontext for user:" + hid));
    	uc.putUserData("{NAME}", e.getProfile().getFirstName());
    	uc.putUserData("{EMAIL}", e.getProfile().getEmail());
    	uc.putUserData("{FAMILY_NAME}", e.getProfile().getLastName());
    	uc.putUserData("{ADDRESS}", e.getProfile().getAddress());
    }
    
    public void receiveMessage(Message m, String hid) {
        log.info("Recieving messages from user:" + hid);
        log.info(m.toString());

        m.header.fromId = new Long(hid);

        MemberChat mc = repo.findByMemberId(hid).orElseThrow(() -> new ResourceNotFoundException("Could not find memberchat."));
        UserContext uc = userrepo.findByMemberId(hid).orElseThrow(() -> new ResourceNotFoundException("Could not find usercontext."));
        
        /*
         * User is onboaring:
         * */
        if(!uc.onboardingComplete()) {
            //OnboardingConversation onboardingConversation = new OnboardingConversation(mc, uc);
        	OnboardingConversationDevi onboardingConversation = new OnboardingConversationDevi(mc, uc, authService);
            onboardingConversation.recieveMessage(m);
        }
       
        /*
         * User is in a claims process:
         * */
        if(uc.ongoingClaimsProcess()){
            ClaimsConversation claimsConversation = new ClaimsConversation(mc, uc);
            claimsConversation.recieveMessage(m);       	
        }
        
        /*
         * User is chatting with Hedvig:
         * */
        if(uc.ongoingMainConversation()) {
        	MainConversation mainConversation = new MainConversation(mc, uc);
            mainConversation.recieveMessage(m);
        }

        repo.saveAndFlush(mc);
        userrepo.saveAndFlush(uc);
    }
}
