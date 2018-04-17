package com.hedvig.botService.chat;

import com.hedvig.botService.enteties.UserContext;
import com.hedvig.botService.enteties.message.Message;
import com.hedvig.botService.enteties.message.MessageBodySingleSelect;
import com.hedvig.botService.enteties.userContextHelpers.UserData;
import com.hedvig.botService.serviceIntegration.memberService.MemberService;
import com.hedvig.botService.session.triggerService.TriggerService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.UUID;

import static com.hedvig.botService.chat.TrustlyConversation.START;
import static com.hedvig.botService.testHelpers.TestData.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class TrustlyConversationTest {

    @Mock
    MemberService memberService;

    @Mock
    TriggerService triggerService;

    @Mock
    ConversationFactory factory;

    private UserContext userContext;

    private TrustlyConversation testConversation;


    @Before
    public void setup() {
        userContext = new UserContext(TOLVANSSON_MEMBER_ID);

        testConversation = new TrustlyConversation(triggerService, factory, memberService);
    }

    @Test
    public void addingStartMessageToChat_initializes_directDebitTrigger() {

        UUID triggerUUID = UUID.randomUUID();

        addTolvansonToUserContext();

        given(triggerService.createTrustlyDirectDebitMandate(TOLVANSSON_SSN, TOLVANSSON_FIRSTNAME, TOLVANSSON_LASTNAME, TOLVANSSON_EMAIL, TOLVANSSON_MEMBER_ID)).willReturn(triggerUUID);

        //ACT
        testConversation.addToChat(START, userContext);

        assertThat(userContext.getDataEntry("{TRUSTLY_TRIGGER_ID}")).isEqualTo(triggerUUID.toString());

    }

    public void addTolvansonToUserContext() {
        final UserData onBoardingData = userContext.getOnBoardingData();
        onBoardingData.setSSN(TOLVANSSON_SSN);
        onBoardingData.setFirstName(TOLVANSSON_FIRSTNAME);
        onBoardingData.setFamilyName(TOLVANSSON_LASTNAME);
        onBoardingData.setEmail(TOLVANSSON_EMAIL);
    }

    @Test
    public void responding_to_START_addNoNewMessageToChat() {
        UUID triggerUUID = UUID.randomUUID();

        final Message message = testConversation.getMessage(START + ".4");
        ((MessageBodySingleSelect)message.body).choices.get(0).selected = true;

        addTolvansonToUserContext();

        given(triggerService.createTrustlyDirectDebitMandate(TOLVANSSON_SSN, TOLVANSSON_FIRSTNAME, TOLVANSSON_LASTNAME, TOLVANSSON_EMAIL, TOLVANSSON_MEMBER_ID)).willReturn(triggerUUID);

        testConversation.receiveMessage(userContext, message);

        assertThat(userContext.getMemberChat().chatHistory.size()).isEqualTo(2);
    }

}