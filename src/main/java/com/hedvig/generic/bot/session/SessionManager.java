package com.hedvig.generic.bot.session;

/*
 * The session manager is the main controller class for the chat service. It contains all user sessions with chat histories, context etc
 * It is a singleton accessed through the request controller
 * */

import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.hedvig.generic.bot.chat.Message;

public class SessionManager {

	private static Logger log = LoggerFactory.getLogger(SessionManager.class);	
	private TreeMap<String,UserContext> userSessions;
	
	@Autowired
	public SessionManager(TreeMap<String, UserContext> sessions){
		userSessions = sessions;
	}
	
	public TreeMap<Long,Message> getMessages(int i, String hid){
		log.info("Getting " + i + " messages for user:" + hid);
		if(!userSessions.containsKey(hid))loadContext(hid); //New user session
		
		return userSessions.get(hid).ch.getLast(i);
	}
	
	public TreeMap<Long,Message> getAllMessages(String hid){
		log.info("Getting all messages for user:" + hid);
		if(!userSessions.containsKey(hid))loadContext(hid); //New user session
		
		return userSessions.get(hid).ch.getHistory();
	}
	
	public void recieveMessage(Message m, String hid){
		log.info("Recieving messages from user:" + hid);
		log.info(m.toString());
		if(!userSessions.containsKey(hid))loadContext(hid); //New user session
		
		userSessions.get(hid).ch.addMessage(System.currentTimeMillis(), m);
		userSessions.get(hid).c.recieveMessage(m);
	}
	
	/*
	 * TODO: Back with peristance
	 * */
	private void loadContext(String hid){
		UserContext uc = new UserContext(hid);
		userSessions.put(hid, uc);
	}
}
