package com.hedvig.botService.chat;

import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.TreeMap;
import java.util.UUID;

public class ChatHistory {

	private static Logger log = LoggerFactory.getLogger(ChatHistory.class);
	private static UUID id = UUID.randomUUID();
	private TreeMap<Long,Message> history = new TreeMap<Long,Message>();
	
	public void addMessage(long t, Message m){
		log.info("Chathistory id: " + id + " Putting message " + m + " in chat history with " + history.size() + " messages");
		history.put(t,m);
	}
	
	public TreeMap<Long,Message> getHistory(){ 
		log.info("Chathistory id: " + id + " Getting message from chat history with " + history.size() + " messages");		
		return history; 
	}
	/*
	 * TODO: There is probably an easier way to retrieve the top K from a TreeMap...
	 * */
	public TreeMap<Long,Message> getLast(double x){
		
		TreeMap<Long,Message> topK = new TreeMap<Long,Message>();
		
		int k = 0;
		for(Entry<Long, Message> n : history.entrySet()){
			topK.put(n.getKey(),n.getValue());
			k++;
			if(k==x)break;
		}
		return topK;
	}
}
