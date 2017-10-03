package com.hedvig.botService.chat;

public abstract class ConversationMessage {

	private Message message;
	abstract Message getNext(Message input);

}
