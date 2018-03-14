package com.hedvig.botService.chat;


import com.hedvig.botService.enteties.MemberChat;
import com.hedvig.botService.enteties.SignupCodeRepository;
import com.hedvig.botService.enteties.UserContext;
import com.hedvig.botService.enteties.message.Message;
import com.hedvig.botService.serviceIntegration.memberService.MemberService;
import com.hedvig.botService.serviceIntegration.productPricing.ProductPricingService;
import com.hedvig.botService.session.events.OnboardingQuestionAskedEvent;
import com.hedvig.botService.session.events.UnderwritingLimitExcededEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ApplicationEventPublisher;

import static com.hedvig.botService.testHelpers.TestData.*;
import static org.mockito.BDDMockito.then;


@RunWith(MockitoJUnitRunner.class)
public class OnboardingConversationDeviTest {

    @Mock
    private MemberService memberService;


    @Mock
    private ProductPricingService productPricingService;

    @Mock
    private SignupCodeRepository signupRepo;

    @Mock
    private ApplicationEventPublisher publisher;

    @Mock
    private ConversationFactory conversationFactory;

    private UserContext userContext;
    private OnboardingConversationDevi testConversation;

    @Before
    public void setup() {
        userContext = new UserContext(TOLVANSSON_MEMBER_ID);
        userContext.setMemberChat(new MemberChat());

        userContext.putUserData("{WEB_USER}", "TRUE");

        testConversation = new OnboardingConversationDevi(memberService, productPricingService, signupRepo, publisher, conversationFactory);
    }

    @Test
    public void SendNotificationEventOn_HouseingUnderWritingLimit(){

        addLastnameToContext(userContext, TOLVANSSON_FIRSTNAME);
        addFirstnametoContext(userContext, TOLVANSSON_LASTNAME);

        Message m = testConversation.getMessage("message.uwlimit.housingsize");
        m.body.text = TOLVANSSON_PHONE_NUMBER;

        testConversation.receiveMessage(userContext, userContext.getMemberChat(), m);

        then(publisher).should().publishEvent(new UnderwritingLimitExcededEvent(
                TOLVANSSON_MEMBER_ID,
                TOLVANSSON_PHONE_NUMBER,
                TOLVANSSON_FIRSTNAME,
                TOLVANSSON_LASTNAME,
                UnderwritingLimitExcededEvent.UnderwritingType.HouseingSize));

    }

    @Test
    public void SendNotificationEventOn_HousholdUnderWritingLimit(){

        addLastnameToContext(userContext, TOLVANSSON_FIRSTNAME);
        addFirstnametoContext(userContext, TOLVANSSON_LASTNAME);

        Message m = testConversation.getMessage("message.uwlimit.householdsize");
        m.body.text = TOLVANSSON_PHONE_NUMBER;

        testConversation.receiveMessage(userContext, userContext.getMemberChat(), m);

        then(publisher).should().publishEvent(new UnderwritingLimitExcededEvent(
                TOLVANSSON_MEMBER_ID,
                TOLVANSSON_PHONE_NUMBER,
                TOLVANSSON_FIRSTNAME,
                TOLVANSSON_LASTNAME,
                UnderwritingLimitExcededEvent.UnderwritingType.HouseholdSize));

    }

    @Test
    public void SendNotificationEventOn_FriFraga(){
        addLastnameToContext(userContext, TOLVANSSON_LASTNAME);
        addFirstnametoContext(userContext, TOLVANSSON_FIRSTNAME);

        Message m = testConversation.getMessage("message.frifraga");
        m.body.text = "I wonder if I can get a home insurance, even thouh my name is Tolvan?";

        testConversation.receiveMessage(userContext, userContext.getMemberChat(), m);

        then(publisher).should().publishEvent(new OnboardingQuestionAskedEvent(TOLVANSSON_MEMBER_ID,
                m.body.text));

    }

}