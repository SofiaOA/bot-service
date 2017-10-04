package com.hedvig.botService.enteties;

import com.hedvig.botService.chat.OnboardingConversation;
import lombok.Getter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class MemberChat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Getter
    private String memberId;

    @OneToMany(mappedBy="chat", cascade = CascadeType.ALL)
    @MapKey(name="timestamp")
    public List<Message> chatHistory;

    @Embedded
    public OnBoardingContext onboardingContext;

    public MemberChat() {

    }

    public MemberChat(String memberId) {
        this.memberId = memberId;
        this.chatHistory = new ArrayList<>();
        this.onboardingContext = new OnBoardingContext();
    }

    public void addToHistory(Message m) {
        m.chat = this;
        this.chatHistory.add(m);
    }

    public void receiveMessage(Message m) {
        addToHistory(m);

        if(!onboardingContext.complete()) {
            OnboardingConversation conversation = new OnboardingConversation(this);
            conversation.recieveMessage(m);
        }
    }

}
