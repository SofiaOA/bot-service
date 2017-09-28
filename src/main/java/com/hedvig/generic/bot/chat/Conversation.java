package com.hedvig.generic.bot.chat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hedvig.generic.bot.session.UserContext;

public abstract class Conversation {

	public static final long  HEDVIG_USER_ID = 1; // The id hedvig uses to chat
	public static final String regexPattern = "\\{(.*?)\\}";
	private static Logger log = LoggerFactory.getLogger(Conversation.class);
	public String conversationId; // Id for the conversation
	public UserContext userContext;
	public TreeMap<String, Message> messageList = new TreeMap<String, Message>();
	public HashMap<String, String> conversationContext = new HashMap<String, String>(); // Context specific information learned during conversation
	
	public Conversation(String conversationId, UserContext u) {
		this.conversationId = conversationId;
		this.userContext = u;
	}
	public String getConversationId() {
		return conversationId;
	}
	public ConversationMessage getCurrent() {
		return current;
	}
	public void setCurrent(ConversationMessage current) {
		this.current = current;
	}
	private ConversationMessage current = null; // Last message sent to client
	
	public String replaceWithContext(String input){
		log.debug("Contextualizing string:" + input);
		Pattern pattern = Pattern.compile(regexPattern);
		Matcher m = pattern.matcher(input);
		while (m.find()) {
			String s = m.group();
			String r = conversationContext.get(s);
			log.debug(s + ":" + r);
			if(r!=null){input = input.replace(s, r);}
		}
		log.debug("-->" + input);
		return input;
	}
	
	public void sendMessage(Message m) {
		log.info("Sending message:" + m.id + " content:" + m.body.content);
		m.body.content = replaceWithContext(m.body.content);
		long t = System.currentTimeMillis();
		m.header.timeStamp = t;
		userContext.chatHistory.addMessage(t, m);
	}

	public void createMessage(String id, MessageHeader header, MessageBody body){
		Message m = new Message();
		m.id = id;
		m.header = header;
		m.body = body;
		messageList.put(m.id, m);
	}
	
	public void createMessage(String id,MessageBody body){
		MessageHeader header = new MessageHeader(Conversation.HEDVIG_USER_ID,"/response",-1); //Default value
		createMessage(id,header,body);
	}
	
	public void startConversation(String startId){
		log.info("Starting conversation with message:" + startId);
		sendMessage(messageList.get(startId));
	}
	
	public abstract void recieveMessage(Message m);
	public abstract void init();
}
