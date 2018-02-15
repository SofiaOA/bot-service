package com.hedvig.botService.session;


import com.hedvig.botService.session.events.RequestPhoneCallEvent;
import com.hedvig.botService.session.events.UnderwritingLimitExcededEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.cloud.aws.messaging.core.NotificationMessagingTemplate;

import static com.hedvig.botService.testHelpers.TestData.*;
import static org.mockito.BDDMockito.then;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.contains;

@RunWith(MockitoJUnitRunner.class)
public class NotificationServiceTest {

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


}