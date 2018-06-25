package com.hedvig.botService.web.v2;

import com.hedvig.botService.services.MessagesService;
import com.hedvig.botService.services.SessionManager;
import com.hedvig.botService.web.v2.dto.MessagesDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController("/v2/messages")
public class MessagesController {

    private static Logger log = LoggerFactory.getLogger(MessagesController.class);

    private final MessagesService messagesService;

    public MessagesController(MessagesService messagesService) {
        this.messagesService = messagesService;
    }

    @GetMapping("/")
    public MessagesDTO getMessages(
            @RequestHeader("hedvig.token") String hid,
            @RequestParam(name = "intent", required = false, defaultValue = "onboarding") SessionManager.Intent intent) {

        return this.messagesService.getMessagesAndStatus(hid, intent);
    }


}
