package com.hedvig.botService.enteties;

import com.hedvig.botService.chat.Conversation;
import com.hedvig.botService.chat.ConversationFactory;
import com.hedvig.botService.enteties.message.Message;
import lombok.Getter;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/*
 * All timestamp information is set from here
 * */

@Entity
//@Table(indexes = {
//        @Index(columnList = "id", name = "conversation_manager_id_idx")
//})
@ToString
public class ConversationManager {

	private static Logger log = LoggerFactory.getLogger(ConversationManager.class);
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Getter
    private String memberId;

    @OneToMany(mappedBy="conversationManager", cascade = CascadeType.ALL, orphanRemoval=true)
    private List<ConversationEntity> conversations;

    @OneToOne(cascade = CascadeType.ALL)
    private ConversationEntity activeConversation;
    
    public ConversationManager() {
        conversations = new ArrayList<>();
    }

    ConversationManager(String memberId) {
    	log.info("Instantiating ConversationManager for member:" + memberId);
        this.memberId = memberId;
        this.conversations = new ArrayList<>();
    }


    
	List<ConversationEntity> getConversations() {
		return conversations;
	}

    boolean startConversation(Class<? extends Conversation> conversationClass) {

        return startConversation(conversationClass, null);
    }

    boolean startConversation(Class<? extends Conversation> conversationClass, String startMessage) {

        for(ConversationEntity c : conversations){
            if(c.getConversationStatus() == Conversation.conversationStatus.ONGOING) {
                if (c.containsConversation(conversationClass)){
                    return false;
                } else {
                    c.setConversationStatus(Conversation.conversationStatus.COMPLETE);
                }
            }
        }

        ConversationEntity conv = new ConversationEntity(this, getMemberId(), conversationClass, startMessage);

        addConversationAndSetActive(conv);

        return true;
    }

    Optional<ConversationEntity> getActiveConversation() {
        if(this.activeConversation == null) {
            return  conversations.
                    stream().
                    filter(x -> x.getConversationStatus() == Conversation.conversationStatus.ONGOING).
                    findFirst();
        }

        return Optional.of(activeConversation);
    }

    private void addConversationAndSetActive(ConversationEntity c) {
        conversations.add(c);
        activeConversation = c;
    }

    void completeConversation(Conversation conversation) {
        if(activeConversation == null) {
            for (ConversationEntity c : getConversations()) {
                if (c.containsConversation(conversation) && c.conversationStatus == Conversation.conversationStatus.ONGOING) {
                    c.conversationStatus = Conversation.conversationStatus.COMPLETE;
                }
            }
        }
        else {
            activeConversation.setConversationStatus(Conversation.conversationStatus.COMPLETE);
        }
    }

    public void receiveEvent(String eventType, String value, ConversationFactory conversationFactory, UserContext userContext) {
        Conversation.EventTypes type = Conversation.EventTypes.valueOf(eventType);


        List<ConversationEntity> conversations = new ArrayList<>(getConversations()); //We will add a new element to uc.conversationManager
        for(ConversationEntity c : conversations){

            // Only deliver messages to ongoing conversations
            if(!c.getConversationStatus().equals(Conversation.conversationStatus.ONGOING))continue;

            try {
                final Class<?> conversationClass = Class.forName(c.getClassName());
                final Conversation conversation = conversationFactory.createConversation(conversationClass);
                conversation.recieveEvent(type, value, userContext);

            } catch (ClassNotFoundException e) {
                log.error("Could not load conversation from db!", e);
            }
        }
    }

    public void receiveMessage(Message m, ConversationFactory conversationFactory, UserContext userContext) {
List<ConversationEntity> conversations = new ArrayList<>(getConversations()); //We will add a new element to uc.conversationManager
        for(ConversationEntity c : conversations){

            // Only deliver messages to ongoing conversations
            if(!c.getConversationStatus().equals(Conversation.conversationStatus.ONGOING))continue;

            try {
                final Class<?> conversationClass = Class.forName(c.getClassName());
                final Conversation conversation = conversationFactory.createConversation(conversationClass);
                conversation.receiveMessage(userContext, m);

            } catch (ClassNotFoundException e) {
                log.error("Could not load conversation from db!", e);
            }
        }
    }
}
