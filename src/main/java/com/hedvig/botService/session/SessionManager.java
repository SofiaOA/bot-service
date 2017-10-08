package com.hedvig.botService.session;

/*
 * The session manager is the main controller class for the chat service. It contains all user sessions with chat histories, context etc
 * It is a singleton accessed through the request controller
 * */

import java.time.Instant;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.hedvig.botService.chat.OnboardingConversation;
import com.hedvig.botService.enteties.MemberChat;
import com.hedvig.botService.enteties.MemberChatRepository;
import com.hedvig.botService.enteties.Message;
import com.hedvig.botService.enteties.ResourceNotFoundException;
import com.hedvig.botService.enteties.UserContext;
import com.hedvig.botService.enteties.UserContextRepository;

public class SessionManager {

    private static Logger log = LoggerFactory.getLogger(SessionManager.class);
    private final MemberChatRepository repo;
    private final UserContextRepository userrepo;

    @Autowired
    public SessionManager( MemberChatRepository repo, UserContextRepository userrepo) {
        this.repo = repo;
        this.userrepo = userrepo;
    }

    public List<Message> getMessages(int i, String hid) {
        log.info("Getting " + i + " messages for user:" + hid);
        List<Message>  messages = getAllMessages(hid);

        return messages.subList(Math.max(messages.size() - i, 0), messages.size());
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

        OnboardingConversation onboardingConversation = new OnboardingConversation(chat, uc);
        
        // If this is the first message the Onboarding conversation is initiated
        if(!uc.onboardingStarted()){
        	uc.onboardingStarted(true);
        	onboardingConversation.init();
        }
        repo.saveAndFlush(chat);
        userrepo.saveAndFlush(uc);
        
        log.info(chat.toString());
        return chat.chatHistory;
    }

    public void receiveMessage(Message m, String hid) {
        log.info("Recieving messages from user:" + hid);
        log.info(m.toString());

        m.header.fromId = new Long(hid);

        MemberChat mc = repo.findByMemberId(hid).orElseThrow(() -> new ResourceNotFoundException("Could not find memberchat."));
        mc.receiveMessage(m);
        UserContext uc = userrepo.findByMemberId(hid).orElseThrow(() -> new ResourceNotFoundException("Could not find usercontext."));
        
        /*
         * User is onboaring:
         * */
        if(!uc.onboardingComplete()) {
            OnboardingConversation onboardingConversation = new OnboardingConversation(mc, uc);
            onboardingConversation.recieveMessage(m);
        }
        
        repo.save(mc);
    }
}
