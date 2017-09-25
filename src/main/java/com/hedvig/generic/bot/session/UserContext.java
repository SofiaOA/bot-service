package com.hedvig.generic.bot.session;

import com.hedvig.generic.bot.chat.ChatHistory;
import com.hedvig.generic.bot.chat.Conversation;
import com.hedvig.generic.bot.chat.OnboardingConversation;
import com.hedvig.generic.bot.chat.SampleConversation;

public class UserContext {
	
	public String userFirstName;
	public String userLastName;
	
	String hedvigToken;
	Conversation c;
	ChatHistory ch;
	
	public UserContext(String hid){
		hedvigToken = hid;
		ch = new ChatHistory();
		c = new OnboardingConversation(ch, this);
		c.init();
	}

}
