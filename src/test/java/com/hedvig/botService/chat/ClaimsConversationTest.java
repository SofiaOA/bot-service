package com.hedvig.botService.chat;

import com.hedvig.botService.enteties.MemberChat;
import com.hedvig.botService.enteties.UserContext;
import com.hedvig.botService.enteties.message.Message;
import com.hedvig.botService.enteties.message.MessageBodyAudio;
import com.hedvig.botService.serviceIntegration.claimsService.ClaimsService;
import com.hedvig.botService.serviceIntegration.productPricing.ProductPricingService;
import com.hedvig.botService.session.events.ClaimAudioReceivedEvent;
import com.hedvig.botService.session.events.ClaimCallMeEvent;
import lombok.val;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ApplicationEventPublisher;

import static com.hedvig.botService.testHelpers.TestData.TOLVANSSON_MEMBER_ID;
import static com.hedvig.botService.testHelpers.TestData.TOLVANSSON_PHONE_NUMBER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.then;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class ClaimsConversationTest {

    public static final String AUDIO_RECORDING_URL = "https://someS3.com/someUUID";
    @Mock
    private
    ApplicationEventPublisher eventPublisher;

    @Mock
    private ClaimsService claimsService;

    @Mock
    private ProductPricingService productPricingService;

    @Mock
    private ConversationFactory conversationFactory;

    private ClaimsConversation testConversation;
    private UserContext userContext;

    @Before
    public void setUp() {

        testConversation = new ClaimsConversation(eventPublisher, claimsService, productPricingService, conversationFactory);
        userContext = new UserContext(TOLVANSSON_MEMBER_ID);
        userContext.setMemberChat(new MemberChat());
    }


    @Test
    public void AudioReceived_SendsClaimAudioReceivedEvent_AndCreatesClaimInClaimsService() {
        Message m = testConversation.getMessage("message.claims.audio");
        val body = (MessageBodyAudio) m.body;
        body.url = AUDIO_RECORDING_URL;
        testConversation.receiveMessage(userContext, userContext.getMemberChat(), m);


        then(eventPublisher).should().publishEvent(new ClaimAudioReceivedEvent(userContext.getMemberId()));
        then(claimsService).should().createClaimFromAudio(anyString(), eq(AUDIO_RECORDING_URL));
    }


    @Test
    public void init_WhenMemberInsuranceIsInactive_StartsNotActiveFlow() {
        when(productPricingService.isMemberInsuranceActive(TOLVANSSON_MEMBER_ID)).thenReturn(false);

        testConversation.init(userContext);

        assertThat(userContext.getMemberChat().chatHistory.get(0).id).isEqualTo(ClaimsConversation.MESSAGE_CLAIMS_NOT_ACTIVE);
    }

    @Test
    public void init_WhenMemberInsuranceIsActive_StartsClaimFlow() {
        when(productPricingService.isMemberInsuranceActive(TOLVANSSON_MEMBER_ID)).thenReturn(true);

        testConversation.init(userContext);

        assertThat(userContext.getMemberChat().chatHistory.get(0).id).isEqualTo(ClaimsConversation.MESSAGE_CLAIMS_START);
    }

    @Test
    public void callMe_WhenMemberInsuranceIsActive_SendsClaimCallMeEventActiveTrue()  {
        when(productPricingService.isMemberInsuranceActive(TOLVANSSON_MEMBER_ID)).thenReturn(true);

        Message m = testConversation.getMessage(ClaimsConversation.MESSAGE_CLAIM_CALLME);
        m.body.text = TOLVANSSON_PHONE_NUMBER;

        testConversation.receiveMessage(userContext, userContext.getMemberChat(), m);

        then(eventPublisher).should().publishEvent(new ClaimCallMeEvent(
                userContext.getMemberId(),
                userContext.getOnBoardingData().getFirstName(),
                userContext.getOnBoardingData().getFamilyName(),
                m.body.text,
                true));
    }

    @Test
    public void callMe_WhenMemberInsuranceIsInactive_SendsClaimCallMeEventActiveFalse()  {
        when(productPricingService.isMemberInsuranceActive(TOLVANSSON_MEMBER_ID)).thenReturn(false);

        Message m = testConversation.getMessage(ClaimsConversation.MESSAGE_CLAIM_CALLME);
        m.body.text = TOLVANSSON_PHONE_NUMBER;

        testConversation.receiveMessage(userContext, userContext.getMemberChat(), m);

        then(eventPublisher).should().publishEvent(new ClaimCallMeEvent(userContext.getMemberId(), userContext.getOnBoardingData().getFirstName(), userContext.getOnBoardingData().getFamilyName(), m.body.text, false));
    }

}