package com.hedvig.botService.session;


import com.hedvig.botService.session.events.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.cloud.aws.messaging.core.NotificationMessagingTemplate;

import static com.hedvig.botService.testHelpers.TestData.*;
import static org.mockito.AdditionalMatchers.and;
import static org.mockito.BDDMockito.then;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.contains;

@RunWith(MockitoJUnitRunner.class)
public class NotificationServiceTest {

    public static final String GOOD_QUESTION = "A long and good question";
    @Mock
    private NotificationMessagingTemplate messagingTemplate;
    private NotificationService notificationService;

    @Before
    public void setup() {
        notificationService = new NotificationService(messagingTemplate);
    }

    @Test
    public void RequestPhoneCall_SendsEventThatContains_PhoneNumer() {

        RequestPhoneCallEvent event = new RequestPhoneCallEvent(TOLVANSSON_MEMBER_ID, TOLVANSSON_PHONE_NUMBER, TOLVANSSON_FIRSTNAME, TOLVANSSON_LASTNAME);
        notificationService.on(event);

        then(messagingTemplate).should().sendNotification(anyString(), contains(TOLVANSSON_PHONE_NUMBER), anyString());
    }

    @Test
    public void UnderwritinglimitExcededEcent_SendsEventThatContains_PhoneNumber() {
        UnderwritingLimitExcededEvent event = new UnderwritingLimitExcededEvent(
                TOLVANSSON_MEMBER_ID,
                TOLVANSSON_PHONE_NUMBER,
                TOLVANSSON_FIRSTNAME,
                TOLVANSSON_LASTNAME,
                UnderwritingLimitExcededEvent.UnderwritingType.HouseingSize);

        notificationService.on(event);

        then(messagingTemplate).should().sendNotification(anyString(), contains(TOLVANSSON_PHONE_NUMBER), anyString());
    }

    @Test
    public void OnboardingQuestionAskedEvent_SendsEventThatContains_MemberId() {
        OnboardingQuestionAskedEvent event = new OnboardingQuestionAskedEvent(
                TOLVANSSON_MEMBER_ID,
                GOOD_QUESTION
        );

        notificationService.on(event);

        then(messagingTemplate).should().sendNotification(anyString(), contains(TOLVANSSON_MEMBER_ID) ,anyString());
    }

    @Test
    public void ClaimAudioReceivedEvent_SendEventThatContains_MembeId() {
        ClaimAudioReceivedEvent event = new ClaimAudioReceivedEvent(TOLVANSSON_MEMBER_ID);

        notificationService.on(event);

        then(messagingTemplate).should().sendNotification(anyString(), contains(TOLVANSSON_MEMBER_ID), anyString());
    }

    @Test
    public void ClaimCallMeEventWithActiveInsurace_SendsEventThatContains_MemberId_InsuranceStatus() {
        ClaimCallMeEvent event = new ClaimCallMeEvent(
                TOLVANSSON_MEMBER_ID,
                TOLVANSSON_FIRSTNAME,
                TOLVANSSON_LASTNAME,
                TOLVANSSON_PHONE_NUMBER,
                true);

        notificationService.on(event);

        then(messagingTemplate).should().sendNotification(anyString(), and(contains(TOLVANSSON_PHONE_NUMBER), contains("AKTIV")), anyString());
    }

    @Test
    public void ClaimCallMeEventWithInactiveInsurace_SendsEventThatContains_MemberId_InsuranceStatus() {
        ClaimCallMeEvent event = new ClaimCallMeEvent(
                TOLVANSSON_MEMBER_ID,
                TOLVANSSON_FIRSTNAME,
                TOLVANSSON_LASTNAME,
                TOLVANSSON_PHONE_NUMBER,
                false);

        notificationService.on(event);

        then(messagingTemplate).should().sendNotification(anyString(), and(contains(TOLVANSSON_PHONE_NUMBER), contains("INAKTIV")), anyString());
    }

    @Test
    public void QuestionAskedEvent_SendsEventThatContains_MemberId() {
        QuestionAskedEvent event = new QuestionAskedEvent(TOLVANSSON_MEMBER_ID, GOOD_QUESTION);

        notificationService.on(event);

        then(messagingTemplate).should().sendNotification(anyString(), contains(TOLVANSSON_MEMBER_ID), anyString());
    }

    @Test
    public void RequestObjectInsuranceEvent_SendsEventThatContains_MemberId() {
        RequestObjectInsuranceEvent event = new RequestObjectInsuranceEvent(TOLVANSSON_MEMBER_ID);

        notificationService.on(event);

        then(messagingTemplate).should().sendNotification(anyString(), contains(TOLVANSSON_MEMBER_ID), anyString());
    }


}