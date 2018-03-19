package com.hedvig.botService.chat;

public interface ConversationFactory {
    Conversation createConversation(Class<?> conversationClass);
    Conversation createConversation(String conversationClassName);
}
