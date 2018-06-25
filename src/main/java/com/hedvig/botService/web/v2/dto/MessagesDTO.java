package com.hedvig.botService.web.v2.dto;

import com.hedvig.botService.enteties.message.Message;
import lombok.Value;

import java.util.List;

@Value
public class MessagesDTO {

    @Value
    public static class State {
        boolean ongoingClaim;
        boolean showOfferScreen;
    }

    State state;

    List<Message> messages;
}


