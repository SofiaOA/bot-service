package com.hedvig.botService.chat;

public interface ConversationFactory {
    Conversation createConversation(Class conversationClass);
}
