package com.hedvig.botService.chat;

import com.hedvig.botService.enteties.MemberChat;
import com.hedvig.botService.enteties.UserContext;
import com.hedvig.botService.enteties.message.Message;
import com.hedvig.botService.session.events.ClaimAudioReceivedEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ApplicationEventPublisher;

import static com.hedvig.botService.testHelpers.TestData.TOLVANSSON_MEMBER_ID;
import static org.mockito.BDDMockito.then;


@RunWith(MockitoJUnitRunner.class)
public class ClaimsConversationTest {

    @Mock
    private
    ApplicationEventPublisher eventPublisher;

    private ClaimsConversation testConversation;
    private UserContext userContext;

    @Before
    public void setUp() {

        testConversation = new ClaimsConversation(eventPublisher);
        userContext = new UserContext(TOLVANSSON_MEMBER_ID);
        userContext.setMemberChat(new MemberChat());
    }


    @Test
    public void AudioReceived_SendsClaimAudioReceivedEvent() {
        Message m = testConversation.getMessage("message.claims.audio");
        m.body.text = "http://somes3.url";

        testConversation.receiveMessage(userContext, userContext.getMemberChat(), m);


        then(eventPublisher).should().publishEvent(new ClaimAudioReceivedEvent(userContext.getMemberId()));
    }

}