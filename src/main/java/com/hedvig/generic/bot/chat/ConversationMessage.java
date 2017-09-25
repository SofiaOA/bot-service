package com.hedvig.generic.bot.chat;

public abstract class ConversationMessage {

	private Message message;
	abstract Message getNext(Message input);

}
