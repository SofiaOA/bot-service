package com.hedvig.botService.enteties;

import lombok.Getter;

import javax.persistence.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hedvig.botService.chat.Conversation;
import com.hedvig.botService.enteties.message.Message;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/*
 * All timestamp information is set from here
 * */

@Entity
public class ConversationManager {

	private static Logger log = LoggerFactory.getLogger(ConversationManager.class);
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Getter
    private String memberId;

    @OneToMany(mappedBy="conversationManager", cascade = CascadeType.ALL, orphanRemoval=true)
    public List<ConversationEntity> conversations;

    public String toString(){	
    	return this.memberId + " " + this.conversations;
    }
    
    public ConversationManager() {
    	log.info("Instantiating ConversationManager " + this );
    	//new Exception().printStackTrace(System.out);
    }

    public ConversationManager(String memberId) {
    	log.info("Instantiating ConversationManager for member:" + memberId);
        this.memberId = memberId;
        this.conversations = new ArrayList<ConversationEntity>();
    }

    public void add(ConversationEntity c) {
    	conversations.add(c);
    }

    /*
     * Check if there is a conversation that needs to be initiated
     * */
    public boolean hasInitiatedConversations(){
    	for(ConversationEntity c: conversations)if(c.conversationStatus.equals(Conversation.conversationStatus.INITIATED))return true;
    	return false;
    }

    /*
     * Check if there is an existing conversation of a certain type with status ONGOING
     * */
    public boolean containsOngoingConversationOfType(String type){
        if(!type.contains(".")) {
            type = ("com.hedvig.botService.chat." + type); // TODO: Refactor/remove hack
        }

    	for(ConversationEntity c : conversations){
    		if(c.getClassName().equals(type) && c.getConversationStatus().equals(Conversation.conversationStatus.ONGOING))return true;
    	}
    	return false;
    }
    
	public List<ConversationEntity> getConversations() {
		return conversations;
	}

	public void setConversations(List<ConversationEntity> conversations) {
		this.conversations = conversations;
	}

}
