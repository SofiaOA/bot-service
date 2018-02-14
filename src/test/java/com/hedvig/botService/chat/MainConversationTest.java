package com.hedvig.botService.chat;

import com.hedvig.botService.enteties.MemberChat;
import com.hedvig.botService.enteties.UserContext;
import com.hedvig.botService.enteties.message.Message;
import com.hedvig.botService.serviceIntegration.productPricing.ProductPricingService;
import com.hedvig.botService.session.events.RequestPhoneCallEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ApplicationEventPublisher;

import static com.hedvig.botService.session.TriggerServiceTest.TOLVANSSON_MEMBERID;
import static org.mockito.BDDMockito.then;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.isA;

@RunWith(MockitoJUnitRunner.class)
public class MainConversationTest {

    @Mock
    ApplicationEventPublisher eventPublisher;

    @Mock
    ConversationFactory conversationFactory;

    @Mock
    MemberChat memberChat;

    @Mock
    ProductPricingService productPricingService;

    MainConversation testConversation;
    UserContext uc;

    @Before
    public void setup() {
        testConversation = new MainConversation(productPricingService, conversationFactory);

        uc = new UserContext(TOLVANSSON_MEMBERID);
        uc.setMemberChat(memberChat);
    }

    @Test
    public void recieveMessage() throws Exception {
        Message m = testConversation.getMessage(MainConversation.MESSAGE_MAIN_CALLME);
        m.body.text = "0701234567";

        testConversation.recieveMessage(uc, memberChat, m);

        then(eventPublisher).should().publishEvent(isA(RequestPhoneCallEvent.class));


    }

}