package com.hedvig.botService.chat;

import com.hedvig.botService.enteties.Message;

public abstract class ConversationMessage {

	private Message message;
	abstract Message getNext(Message input);

}
