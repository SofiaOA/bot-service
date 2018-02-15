package com.hedvig.botService.chat;

import com.hedvig.botService.enteties.MemberChat;
import com.hedvig.botService.enteties.UserContext;
import com.hedvig.botService.enteties.message.Message;
import com.hedvig.botService.serviceIntegration.productPricing.ProductPricingService;
import com.hedvig.botService.session.events.QuestionAskedEvent;
import com.hedvig.botService.session.events.RequestPhoneCallEvent;
import com.hedvig.botService.testHelpers.TestData;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ApplicationEventPublisher;

import static com.hedvig.botService.testHelpers.TestData.*;
import static org.mockito.BDDMockito.then;

@RunWith(MockitoJUnitRunner.class)
public class MainConversationTest {

    public static final String QUESTION = "A short but sweet question";
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
        testConversation = new MainConversation(productPricingService, conversationFactory, eventPublisher);

        uc = new UserContext(TOLVANSSON_MEMBER_ID);
        uc.setMemberChat(memberChat);
    }

    @Test
    public void recieveMessage() throws Exception {
        Message m = testConversation.getMessage(MainConversation.MESSAGE_MAIN_CALLME);
        m.body.text = TOLVANSSON_PHONE_NUMBER;

        uc.getOnBoardingData().setFirstName(TOLVANSSON_FIRSTNAME);
        uc.getOnBoardingData().setFamilyName(TOLVANSSON_LASTNAME);

        testConversation.recieveMessage(uc, memberChat, m);

        then(eventPublisher).should().publishEvent(new RequestPhoneCallEvent(TOLVANSSON_MEMBER_ID, TOLVANSSON_PHONE_NUMBER , TOLVANSSON_FIRSTNAME, TOLVANSSON_LASTNAME));
    }

    @Test
    public void GIVEN_MessageQuestion_SHOULD_sendQuestionAskedEvent() {

        Message m = testConversation.getMessage(MainConversation.MESSAGE_MAIN_QUESTION);
        m.body.text = QUESTION;

        testConversation.recieveMessage(uc, memberChat, m);

        then(eventPublisher).should().publishEvent(new QuestionAskedEvent(TOLVANSSON_MEMBER_ID, QUESTION));


    }

}