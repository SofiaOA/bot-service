package com.hedvig.botService.session;

/*
 * The session manager is the main controller class for the chat service. It contains all user sessions with chat histories, context etc
 * It is a singleton accessed through the request controller
 * */

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.hedvig.botService.chat.OnboardingConversation;
import com.hedvig.botService.enteties.MemberChat;
import com.hedvig.botService.enteties.MemberChatRepository;
import com.hedvig.botService.enteties.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.hedvig.botService.enteties.Message;

public class SessionManager {

    private static Logger log = LoggerFactory.getLogger(SessionManager.class);
    private final MemberChatRepository repo;

    @Autowired
    public SessionManager( MemberChatRepository repo) {
        this.repo = repo;
    }

    public List<Message> getMessages(int i, String hid) {
        log.info("Getting " + i + " messages for user:" + hid);
        List<Message>  messages = getAllMessages(hid);

        return messages.subList(Math.max(messages.size() - i, 0), messages.size());
    }

    public List<Message> getAllMessages(String hid) {
        log.info("Getting all messages for user:" + hid);

        MemberChat chat = repo.findByMemberId(hid)
                .orElseGet(() -> {
                    MemberChat newChat = new MemberChat(hid);
                    repo.save(newChat);
                    OnboardingConversation onboardingConversation = new OnboardingConversation(newChat);
                    onboardingConversation.init();
                    return newChat;
                });

        return chat.chatHistory;
    }

    public void receiveMessage(Message m, String hid) {
        log.info("Recieving messages from user:" + hid);
        log.info(m.toString());

        m.header.fromId = new Long(hid);
        m.setTimestamp(Instant.now());

        MemberChat chat = repo.findByMemberId(hid).orElseThrow(() -> new ResourceNotFoundException("Could not find memberchat."));

        chat.receiveMessage(m);
        repo.save(chat);
    }
}
