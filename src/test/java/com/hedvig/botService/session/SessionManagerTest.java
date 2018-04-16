package com.hedvig.botService.session;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.hedvig.botService.chat.Conversation;
import com.hedvig.botService.chat.ConversationFactory;
import com.hedvig.botService.enteties.MemberChat;
import com.hedvig.botService.enteties.SignupCodeRepository;
import com.hedvig.botService.enteties.UserContext;
import com.hedvig.botService.enteties.UserContextRepository;
import com.hedvig.botService.enteties.message.Message;
import com.hedvig.botService.enteties.message.MessageBodySingleSelect;
import com.hedvig.botService.enteties.message.SelectLink;
import com.hedvig.botService.serviceIntegration.memberService.MemberService;
import com.hedvig.botService.serviceIntegration.productPricing.ProductPricingService;
import com.hedvig.botService.web.dto.AddMessageRequestDTO;
import lombok.val;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Optional;

import static com.hedvig.botService.chat.Conversation.HEDVIG_USER_ID;
import static com.hedvig.botService.session.TriggerServiceTest.TOLVANSSON_MEMBERID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class SessionManagerTest {

    public static final String MESSAGE = "Heh hej";
    public static final SelectLink SELECT_LINK = SelectLink.toOffer("Offer", "offer");
    @Mock
    UserContextRepository userContextRepository;

    @Mock
    MemberService memberService;

    @Mock
    ProductPricingService productPricingService;

    @Mock
    SignupCodeRepository signupCodeRepository;

    @Mock
    ConversationFactory conversationFactory;

    @Mock
    Conversation mockConversation;


    SessionManager sessionManager;

    @Before
    public void setUp() {
        sessionManager = new SessionManager(userContextRepository, memberService, productPricingService, signupCodeRepository, conversationFactory);
    }

    @Test
    public void givenConversationThatCanAcceptMessage_WhenAddMessageFromHedvig_ThenAddsMessageToHistory() {

        val tolvanssonUserContext = makeTolvanssonUserContext();

        when(userContextRepository.findByMemberId(TOLVANSSON_MEMBERID)).thenReturn(Optional.of(tolvanssonUserContext));
        when(conversationFactory.createConversation(anyString())).thenReturn(mockConversation);

        when(mockConversation.canAcceptAnswerToQuestion(tolvanssonUserContext)).thenReturn(true);
        when(mockConversation.getSelectItemsForAnswer(tolvanssonUserContext)).thenReturn(Lists.newArrayList(SELECT_LINK));

        AddMessageRequestDTO requestDTO = new AddMessageRequestDTO(TOLVANSSON_MEMBERID, MESSAGE);

        val messageCouldBeAdded = sessionManager.addMessageFromHedvig(requestDTO);

        assertThat(messageCouldBeAdded).isTrue();

        Message message = Iterables.getLast(tolvanssonUserContext.getMemberChat().chatHistory);
        assertThat(message.body.text).isEqualTo(MESSAGE);
        assertThat(message.id).isEqualTo("message.bo.message");
        assertThat(message.header.fromId).isEqualTo(HEDVIG_USER_ID);
        assertThat(((MessageBodySingleSelect)message.body).choices).containsExactly(SELECT_LINK);
    }

    @Test
    public void givenConversationThatCanAcceptMessage_WhenAddMessageFromHedvig_ThenReturnFalse() {

        val tolvanssonUserContext = makeTolvanssonUserContext();

        when(userContextRepository.findByMemberId(TOLVANSSON_MEMBERID)).thenReturn(Optional.of(tolvanssonUserContext));
        when(mockConversation.canAcceptAnswerToQuestion(tolvanssonUserContext)).thenReturn(false);
        when(conversationFactory.createConversation(anyString())).thenReturn(mockConversation);

        AddMessageRequestDTO requestDTO = new AddMessageRequestDTO(TOLVANSSON_MEMBERID, MESSAGE);

        val messageCouldBeAdded = sessionManager.addMessageFromHedvig(requestDTO);

        assertThat(messageCouldBeAdded).isFalse();
    }

    public UserContext makeTolvanssonUserContext() {
        val tolvanssonUserContext = new UserContext(TOLVANSSON_MEMBERID);
        tolvanssonUserContext.setMemberChat(new MemberChat());
        tolvanssonUserContext.startConversation(mockConversation);
        return  tolvanssonUserContext;
    }

}