package com.hedvig.botService.session;

import com.hedvig.botService.session.events.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.aws.messaging.core.NotificationMessagingTemplate;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Profile("production")
public class NotificationService {

    private final NotificationMessagingTemplate template;
    private final Logger log = LoggerFactory.getLogger(NotificationService.class);

    public NotificationService(NotificationMessagingTemplate template) {

        this.template = template;
    }

    @EventListener
    public void on(SignedOnWaitlistEvent evt) {
        sendNotification("Ny person på väntelistan! " + evt.getEmail(), "PersonOnWaitlist");
    }

    @EventListener
    public void on(RequestPhoneCallEvent evt) {
        final String message = String.format("Medlem %s %s vill bli kontaktad på %s", evt.getFirstName(), evt.getLastName(), evt.getPhoneNumber());
        sendNotification(message, "CallMe");
    }

    @EventListener
    public void on(UnderwritingLimitExcededEvent event) {
        final String message = String.format("Underwriting guideline, ring upp medlem: %s", event.getPhoneNumber());
        sendNotification(message, "CallMe");
    }


    @EventListener
    public void on(OnboardingQuestionAskedEvent event) {
        final String message = String.format("Ny fråga från onboarding-medlem: %s, \"%s\"", event.getMemberId(), event.getQuestion());
        sendNotification(message, "CallMe");
    }

    @EventListener
    public void on(ClaimAudioReceivedEvent event) {
        final String message = String.format("Ny skadeanmälan ifrån medlem: %s", event.getMemberId());
        sendNotification(message, "CallMe");
    }

    @EventListener
    public void on(QuestionAskedEvent event) {
        final String message = String.format("Ny fråga från medlem: %s, \"%s\".", event.getMemberId(), event.getQuestion());
        sendNotification(message, "CallMe");
    }

    private void sendNotification(String message, String subject) {
        try {
            template.sendNotification("newMembers", message, subject);
        }catch (Exception ex) {
            log.error("Could not send SNS-notification", ex);
        }
    }
}