package com.hedvig.botService.enteties;

import com.hedvig.botService.chat.Conversation;
import com.hedvig.botService.enteties.message.Message;
import lombok.Getter;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/*
 * All timestamp information is set from here
 * */

@Entity
@ToString(exclude = "userContext")
public class MemberChat {

	private static Logger log = LoggerFactory.getLogger(MemberChat.class);
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Getter
    private String memberId;

    @OneToMany(mappedBy="chat", cascade = CascadeType.ALL, orphanRemoval=true) // TODO kolla att detta funkar
    @MapKey(name="timestamp")
    public List<Message> chatHistory;

    @OneToOne()
	public UserContext userContext;
    
    public MemberChat() {
    	chatHistory = new ArrayList<>();
    	//new Exception().printStackTrace(System.out);
    }

	public MemberChat(String memberId) {
		log.info("Instantiating MemberChat for member:" + memberId);
		this.memberId = memberId;
		this.chatHistory = new ArrayList<>();
	}

    /*
     * Removes (by marking them as deleted) all messages
     * */
    public void reset(){

    	chatHistory.clear(); // TODO: Make non delete solutions
    	return;
    	
    	/*
    	for(Message m : chatHistory){
    		log.info("Mark deleted:" + m.globalId + " " + m);
    		m.deleted = true;
    	}
    	*/
    	
    }
    
    /*
     * Removes (by marking them as deleted) all messages until the last point of user input
     * */
    public void revertLastInput(){
    	Collections.sort(chatHistory, new Comparator<Message>(){
    	     public int compare(Message m1, Message m2){
    	         if(m1.globalId == m2.globalId)
    	             return 0;
    	         return m1.globalId > m2.globalId ? -1 : 1;
    	     }
    	});
    	
    	/*
    	 * If there is no input message to revert to yet then leave the chat as is
    	 * */
    	boolean hasUserInput = false;
    	for(Message m : chatHistory){if(!m.deleted && m.header.fromId != Conversation.HEDVIG_USER_ID){hasUserInput = true;break;}}
    	if(!hasUserInput)return;
    	
    	for(Message m : chatHistory){
    		m.deleted = true;
    		if(!(m.header.fromId == Conversation.HEDVIG_USER_ID)){break;}
    	}
    	
    }
    
    /*
     * Mark ONLY last input from user as editAllowed -> pen symbol in client
     * */
    private void markLastInput(){
    	Collections.sort(chatHistory, (m1, m2) -> {
            if(m1.globalId == m2.globalId)
                return 0;
            return m1.globalId > m2.globalId ? -1 : 1;
        });
    	/*
    	 * If there is no input message to revert to yet then leave the chat as is
    	 * */
    	boolean hasUserInput = false;
    	for(Message m : chatHistory){if(!m.deleted && m.header.fromId != Conversation.HEDVIG_USER_ID){hasUserInput = true; m.header.editAllowed=false;}}
    	if(!hasUserInput)return;
    	
    	for(Message m : chatHistory){
    		if(!(m.header.fromId == Conversation.HEDVIG_USER_ID)){m.header.editAllowed = true; break;}
    	}
    }

    public void addToHistory(Message m) {
    	log.info("MemberChat.addToHistory(Message: " + m + " ," + "chat:" + this);
		Instant time = Instant.now();
		m.deleted = false;
		m.setTimestamp(time);
		m.header.timeStamp = time.toEpochMilli();
        m.chat = this;
        this.chatHistory.add(m);
    }

    public List<Message> getMessages() {
		// Mark last user input with as editAllowed
    	this.markLastInput();

		// Check for deleted messages
		ArrayList<Message> returnList = new ArrayList<>();
		for(Message m : this.chatHistory){
			if(m.deleted==null || !m.deleted){
				returnList.add(m);
			}
		}

		return returnList;
	}

	public Message getLastMessage() {
    	return chatHistory.get(chatHistory.size());
    }
}
