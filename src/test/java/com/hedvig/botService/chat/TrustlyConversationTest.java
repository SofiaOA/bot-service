package com.hedvig.botService.chat;

import com.hedvig.botService.enteties.MemberChat;
import com.hedvig.botService.enteties.UserContext;
import com.hedvig.botService.enteties.message.Message;
import com.hedvig.botService.enteties.userContextHelpers.UserData;
import com.hedvig.botService.serviceIntegration.memberService.MemberService;
import com.hedvig.botService.serviceIntegration.paymentService.PaymentService;
import com.hedvig.botService.serviceIntegration.productPricing.ProductPricingService;
import com.hedvig.botService.session.triggerService.TriggerService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.UUID;

import static com.hedvig.botService.testHelpers.TestData.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class TrustlyConversationTest {

    @Mock
    MemberService memberService;

    @Mock
    ProductPricingService productPricingService;

    @Mock
    TriggerService triggerService;

    private UserContext userContext;
    private TrustlyConversation testConversation;


    @Before
    public void setup() {
        userContext = new UserContext(TOLVANSSON_MEMBER_ID);
        userContext.setMemberChat(new MemberChat());

        testConversation = new TrustlyConversation(memberService, productPricingService, triggerService);
    }

    @Test
    public void addingStartMessageToChat_initializes_directDebitTrigger() {

        UUID triggerUUID = UUID.randomUUID();

        final UserData onBoardingData = userContext.getOnBoardingData();
        onBoardingData.setSSN(TOLVANSSON_SSN);
        onBoardingData.setFirstName(TOLVANSSON_FIRSTNAME);
        onBoardingData.setFamilyName(TOLVANSSON_LASTNAME);
        onBoardingData.setEmail(TOLVANSSON_EMAIL);

        given(triggerService.createDirectDebitMandate(TOLVANSSON_SSN, TOLVANSSON_FIRSTNAME, TOLVANSSON_LASTNAME, TOLVANSSON_EMAIL, TOLVANSSON_MEMBER_ID)).willReturn(triggerUUID);

        //ACT
        testConversation.addToChat(TrustlyConversation.START, userContext);

        assertThat(userContext.getDataEntry("{TRUSTLY_TRIGGER_ID}")).isEqualTo(triggerUUID.toString());

    }

}