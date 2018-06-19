package com.hedvig.botService.chat;

import com.google.common.collect.Lists;
import com.hedvig.botService.enteties.UserContext;
import com.hedvig.botService.enteties.message.*;
import lombok.val;

import java.util.List;

public class FreeChatConversation extends Conversation {

    private static final String FREE_CHAT_START = "free.chat.start";
    private static final String FREE_CHAT_MESSAGE = "free.chat.message";
    public static final String FREE_CHAT_FROM_BO = "free.chat.from.bo";

    public FreeChatConversation() {
        createMessage(FREE_CHAT_START,
                new MessageBodyText("Hej {NAME}, vad har du för fråga?"));

        createMessage(FREE_CHAT_MESSAGE,
                new MessageBodyText(""));
    }

    public Message addMessage(final String message, UserContext uc) {
        Message msg = new Message();

        msg.body =  new MessageBodyText(message);
        msg.header.fromId = Conversation.HEDVIG_USER_ID;
        msg.id = FREE_CHAT_FROM_BO;
        uc.getMemberChat().addToHistory(msg);

        return msg;
    }

    @Override
    public List<SelectItem> getSelectItemsForAnswer(UserContext uc) {
        return Lists.newArrayList();
    }

    @Override
    public boolean canAcceptAnswerToQuestion(UserContext uc) {
        return false;
    }

    @Override
    public void receiveMessage(UserContext userContext, Message m) {
        String nxtMsg = "";

        switch (m.id) {
            case FREE_CHAT_START: {
                addToChat(m, userContext);
                nxtMsg = FREE_CHAT_MESSAGE;
                break;
            }
            case FREE_CHAT_MESSAGE: {
                addToChat(m, userContext);
                nxtMsg = FREE_CHAT_MESSAGE;
                break;
            }
        }

        /*
         * In a Single select, there is only one trigger event. Set default here to be a link to a new message
         */
        if (nxtMsg.equals("") && m.body.getClass().equals(MessageBodySingleSelect.class)) {

            MessageBodySingleSelect body1 = (MessageBodySingleSelect) m.body;
            for (SelectItem o : body1.choices) {
                if(o.selected) {
                    m.body.text = o.text;
                    addToChat(m, userContext);
                    nxtMsg = o.value;
                }
            }
        }

        completeRequest(nxtMsg, userContext);
    }

    @Override
    public void init(UserContext userContext) {
        startConversation(userContext, FREE_CHAT_START); // Id of first message
    }

    @Override
    public void init(UserContext userContext, String startMessage) {
        startConversation(userContext, startMessage); // Id of first message
    }
}
