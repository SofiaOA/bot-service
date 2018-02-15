package com.hedvig.botService.enteties;

import com.hedvig.botService.chat.Conversation;
import lombok.Getter;

import javax.persistence.*;

/*
 * Stores persistent properties for a Conversation
 * */

@Entity
@Table(indexes = {
		@Index(columnList = "id", name = "conversation_entity_id_idx"),
		@Index(columnList = "conversation_manager_id", name= "conversation_entity_manager_id_idx")
})
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
