package com.hedvig.generic.bot.chat;

import java.util.Map.Entry;
import java.util.TreeMap;

public class ChatHistory {

	private TreeMap<Long,Message> history = new TreeMap<Long,Message>();
	
	public void addMessage(long t, Message m){history.put(t,m);}
	
	public TreeMap<Long,Message> getHistory(){ return history; }
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
