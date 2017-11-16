package com.hedvig.botService.enteties;

import com.hedvig.botService.chat.Conversation;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/*
 * All timestamp information is set from here
 * */

@Entity
//@Table(indexes = {
//        @Index(columnList = "id", name = "conversation_manager_id_idx")
//})
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

    private void addConversation(ConversationEntity c) {
        c.setConversationManager(this);
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

    public boolean startConversation(Class<? extends Conversation> conversationClass) {

        return startConversation(conversationClass, null);
    }

    public boolean startConversation(Class<? extends Conversation> conversationClass, String startMessage) {

        for(ConversationEntity c : conversations){
            if(c.getConversationStatus().equals(Conversation.conversationStatus.ONGOING)) {
                if (c.getClassName().equals(conversationClass.getName())) {
                    return false;
                } else {
                    c.setConversationStatus(Conversation.conversationStatus.COMPLETE);
                }
            }
        }

        ConversationEntity conv = new ConversationEntity();
        conv.setClassName(conversationClass.getName());
        conv.setMemberId(getMemberId());
        conv.setConversationStatus(Conversation.conversationStatus.ONGOING);
        if(startMessage != null) {
            conv.setStartMessage(startMessage);
        }

        addConversation(conv);

        return true;
    }
}
