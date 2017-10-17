package com.hedvig.botService.enteties;

import lombok.Getter;

import javax.persistence.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/*
 * All timestamp information is set from here
 * */

@Entity
public class MemberChat {

	private static Logger log = LoggerFactory.getLogger(MemberChat.class);
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Getter
    private String memberId;

    @OneToMany(mappedBy="chat", cascade = CascadeType.ALL) // TODO kolla att detta funkar
    @MapKey(name="timestamp")
    public List<Message> chatHistory;

    /*@Embedded
    public UserContext onboardingContext;*/

    public String toString(){
    	
    	String mId = "";
    	if(chatHistory != null)for(Message m : chatHistory){mId += (" (" + m.globalId + ":" + m.id + ")");}
    	return "id:" + this.id + " memberId:" + this.memberId + " #msgs:" + (chatHistory == null? null:chatHistory.size() + " [" + mId + "]");
    }
    
    public MemberChat() {
    	log.info("Instantiating MemberChat " + this );
    	//new Exception().printStackTrace(System.out);
    }

    public MemberChat(String memberId) {
    	log.info("Instantiating MemberChat for member:" + memberId + " :" + this );
        this.memberId = memberId;
        this.chatHistory = new ArrayList<Message>();
        //this.onboardingContext = new UserContext();
    }

    public void addToHistory(Message m) {
    	log.info("MemberChat.addToHistory(Message: " + m + " ," + "chat:" + this);
		Instant time = Instant.now();
		m.setTimestamp(time);
		m.header.timeStamp = time.toEpochMilli();
        m.chat = this;
        this.chatHistory.add(m);
    }

}
