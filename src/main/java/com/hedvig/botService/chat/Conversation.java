package com.hedvig.botService.chat;

import java.time.Instant;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hedvig.botService.enteties.MemberChat;
import com.hedvig.botService.enteties.Message;
import com.hedvig.botService.enteties.MessageBody;
import com.hedvig.botService.enteties.MessageHeader;
import com.hedvig.botService.enteties.UserContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Conversation {

    static final long  HEDVIG_USER_ID = 1; // The id hedvig uses to chat
	private static final String regexPattern = "\\{(.*?)\\}";
	private static Logger log = LoggerFactory.getLogger(Conversation.class);
	private String conversationName; // Id for the conversation
	MemberChat memberChat;
	UserContext userContext;
	TreeMap<String, Message> messageList = new TreeMap<String, Message>();
	//HashMap<String, String> conversationContext = new HashMap<String, String>(); // Context specific information learned during conversation
	
	Conversation(String conversationId, MemberChat mc, UserContext uc) {
		this.conversationName = conversationId;
		this.memberChat = mc;
		this.userContext = uc;
	}

	public String getConversationId() {
		return conversationName;
	}
	public ConversationMessage getCurrent() {
		return current;
	}
	public void setCurrent(ConversationMessage current) {
		this.current = current;
	}
	private ConversationMessage current = null; // Last message sent to client
	
	private String replaceWithContext(String input){
		log.debug("Contextualizing string:" + input);
		Pattern pattern = Pattern.compile(regexPattern);
		Matcher m = pattern.matcher(input);
		while (m.find()) {
			String s = m.group();
			String r = userContext.getDataEntry(s);
			log.debug(s + ":" + r);
			if(r!=null){input = input.replace(s, r);}
		}
		log.debug("-->" + input);
		return input;
	}
	
	void putMessage(Message m) {
		log.info("Putting message:" + m.id + " content:" + m.body.text);
		m.body.text = replaceWithContext(m.body.text);
		memberChat.addToHistory(m);
	}

	private void createMessage(String id, MessageHeader header, MessageBody body){
		Message m = new Message();
		m.id = id;
		m.header = header;
		m.body = body;
		messageList.put(m.id, m);
	}
	
	void createMessage(String id, MessageBody body){
		MessageHeader header = new MessageHeader(Conversation.HEDVIG_USER_ID,"/response",-1); //Default value
		createMessage(id,header,body);
	}
	
	void startConversation(String startId){
		log.info("Starting conversation with message:" + startId);
		putMessage(messageList.get(startId));
	}
	
	public abstract void recieveMessage(Message m);
	public abstract void init();
}
