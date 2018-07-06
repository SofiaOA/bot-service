
package com.hedvig.botService.enteties;

import com.hedvig.botService.chat.Conversation;
import lombok.Getter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Objects;

/*
 * Stores persistent properties for a Conversation
 * */

@Entity
@Table(indexes = {
		@Index(columnList = "id", name = "conversation_entity_id_idx"),
		@Index(columnList = "conversation_manager_id", name= "conversation_entity_manager_id_idx")
})
@ToString(exclude = "conversationManager")
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

	public ConversationEntity() {}

	public ConversationEntity(ConversationManager conversationManager, String memberId, Class<? extends Conversation> conversationClass, String startMessage) {
		this.className = conversationClass.getName();
		this.memberId = memberId;
		this.setConversationStatus(Conversation.conversationStatus.ONGOING);
		this.conversationManager = conversationManager;
		if(startMessage != null) {
			this.setStartMessage(startMessage);
		}
	}

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

	public String getStartMessage() {
		return startMessage;
	}

	public void setStartMessage(String startMessage) {
		this.startMessage = startMessage;
	}

	public ConversationManager getConversationManager() {
		return conversationManager;
	}

	public boolean containsConversation(Conversation conversation) {
		String conversationClassName = conversation.getClass().getName();
		return Objects.equals(this.getClassName(), conversationClassName);
	}

	public boolean containsConversation(Class<? extends Conversation> conversationClass) {
		String conversationClassName = conversationClass.getName();
		return Objects.equals(this.getClassName(), conversationClassName);
	}
}
