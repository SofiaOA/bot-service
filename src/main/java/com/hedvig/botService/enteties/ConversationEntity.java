package com.hedvig.botService.enteties;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;

import com.hedvig.botService.chat.Conversation;

import lombok.Getter;

/*
 * Stores persistent properties for a Conversation
 * */

@Entity
public class ConversationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Getter
    private String memberId;
	
    @ManyToOne()
    private ConversationManager conversationManager;
    
    public Conversation.conversationStatus conversationStatus;
    
    private String className;

    private String startMessage; // Optional starting point in conversation
    
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Conversation.conversationStatus getConversationStatus() {
		return conversationStatus;
	}

	public void setConversationStatus(Conversation.conversationStatus conversationStatus) {
		this.conversationStatus = conversationStatus;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

	public String getStartMessage() {
		return startMessage;
	}

	public void setStartMessage(String startMessage) {
		this.startMessage = startMessage;
	}

	public ConversationManager getConversationManager() {
		return conversationManager;
	}

	public void setConversationManager(ConversationManager conversationManager) {
		this.conversationManager = conversationManager;
	}
}
