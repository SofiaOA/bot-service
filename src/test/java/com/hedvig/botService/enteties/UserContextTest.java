package com.hedvig.botService.enteties;

import static com.hedvig.botService.testHelpers.TestData.TOLVANSSON_MEMBER_ID;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.hedvig.botService.chat.Conversation;
import com.hedvig.botService.chat.ConversationFactory;
import com.hedvig.botService.chat.OnboardingConversationDevi;
import com.hedvig.botService.services.SessionManager.Intent;
import lombok.val;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class UserContextTest {

  @Mock ConversationFactory conversationFactory;
  @Mock
  Conversation mockConversation;

  @Test
  public void getMessages_withIntentOnboarding_callsInitWithCorrectStartMessage() {


    given(conversationFactory.createConversation(OnboardingConversationDevi.class)).willReturn(mockConversation);

    val uc = new UserContext(TOLVANSSON_MEMBER_ID);

    uc.getMessages(Intent.ONBOARDING, conversationFactory);

    then(mockConversation).should(times(1)).init(uc, OnboardingConversationDevi.MESSAGE_ONBOARDINGSTART_SHORT);

  }


}
