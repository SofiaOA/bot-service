package com.hedvig.botService.session;

import com.hedvig.botService.chat.ChatHistory;
import com.hedvig.botService.chat.Conversation;
import com.hedvig.botService.chat.OnboardingConversation;

public class UserContext {
	
	public String userFirstName;
	public String userLastName;
	
	String hedvigToken;
	Conversation c;
	public ChatHistory chatHistory;
	
	public UserContext(String hid){
		hedvigToken = hid;
		chatHistory = new ChatHistory();
		//c = new OnboardingConversation(this);
		c.init();
	}

}
