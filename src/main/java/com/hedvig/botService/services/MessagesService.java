package com.hedvig.botService.services;

import com.google.common.collect.Lists;
import com.hedvig.botService.chat.ClaimsConversation;
import com.hedvig.botService.chat.ConversationFactory;
import com.hedvig.botService.chat.FreeChatConversation;
import com.hedvig.botService.enteties.ResourceNotFoundException;
import com.hedvig.botService.enteties.UserContext;
import com.hedvig.botService.enteties.UserContextRepository;
import com.hedvig.botService.enteties.message.Message;
import com.hedvig.botService.serviceIntegration.claimsService.ClaimsService;
import com.hedvig.botService.web.v2.dto.FABAction;
import com.hedvig.botService.web.v2.dto.MessagesDTO;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;

@Component
@Transactional
public class MessagesService {

    private final static Logger log = LoggerFactory.getLogger(MessagesService.class);
    private final UserContextRepository userContextRepository;
    private final ConversationFactory conversationFactory;
    private final ClaimsService claimsService;

    public MessagesService(UserContextRepository userContextRepository, ConversationFactory conversationFactory, ClaimsService claimsService) {
        this.userContextRepository = userContextRepository;
        this.conversationFactory = conversationFactory;
        this.claimsService = claimsService;
    }


    public MessagesDTO getMessagesAndStatus(String hid, SessionManager.Intent intent) {
        UserContext uc = userContextRepository.findByMemberId(hid).orElseThrow(() -> new ResourceNotFoundException("Could not find usercontext."));

        val messages = uc.getMessages(intent, conversationFactory);
        messages.sort(Comparator.comparing(Message::getGlobalId).reversed());

        val signed = uc.getOnBoardingData().getUserHasSigned();
        val hasClaim = this.claimsService.getActiveClaims(hid) > 0;

        final String triggerUrl = "/v2/app/fabTrigger/%s";
        val options = Lists.newArrayList(
                new MessagesDTO.FABOption("Prata med Hedvig 💬", String.format(triggerUrl, FABAction.CHAT.name()), true),
                new MessagesDTO.FABOption("Det är kris ring mig 📞", String.format(triggerUrl, FABAction.CALL_ME.name()), true),
                new MessagesDTO.FABOption("Anmäl en skada", String.format(triggerUrl, FABAction.REPORT_CLAIM.name()), false)
        );
        return new MessagesDTO(
                new MessagesDTO.State(hasClaim, signed == false),
                messages,
                options
        );
    }

    public ResponseEntity<?> fabTrigger(String hid, FABAction actionId) {

        UserContext uc = userContextRepository.findByMemberId(hid).orElseThrow(() -> new ResourceNotFoundException("Could not find usercontext."));

        switch (actionId) {
            case CHAT:
                uc.startConversation(conversationFactory.createConversation(FreeChatConversation.class));
                break;
            case CALL_ME:
                uc.startConversation(conversationFactory.createConversation(FreeChatConversation.class));
                break;
            case REPORT_CLAIM:
                uc.startConversation(conversationFactory.createConversation(ClaimsConversation.class));
                break;
        }

        return ResponseEntity.accepted().build();
    }
}
