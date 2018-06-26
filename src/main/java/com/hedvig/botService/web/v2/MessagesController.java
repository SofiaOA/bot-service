package com.hedvig.botService.web.v2;

import com.hedvig.botService.services.MessagesService;
import com.hedvig.botService.services.SessionManager;
import com.hedvig.botService.web.v2.dto.MessagesDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController("messagesControllerV2")
@RequestMapping("/v2/messages")
public class MessagesController {

    private static Logger log = LoggerFactory.getLogger(MessagesController.class);

    private final MessagesService messagesService;

    public MessagesController(MessagesService messagesService) {
        this.messagesService = messagesService;
    }

    @GetMapping("/")
    public MessagesDTO getMessages(
            @RequestHeader("hedvig.token") String hid,
            @RequestParam(name = "intent", required = false, defaultValue = "onboarding") String intentParameter) {

        SessionManager.Intent intent = Objects.equals(intentParameter, "login") ? SessionManager.Intent.LOGIN : SessionManager.Intent.ONBOARDING;
        return this.messagesService.getMessagesAndStatus(hid, intent);
    }


}
