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
	public ChatHistory chatHistory;
	
	public UserContext(String hid){
		hedvigToken = hid;
		chatHistory = new ChatHistory();
		c = new OnboardingConversation(this);
		c.init();
	}

}
