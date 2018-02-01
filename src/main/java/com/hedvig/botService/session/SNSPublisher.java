package com.hedvig.botService.session;

import com.amazonaws.services.sns.AmazonSNS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.aws.messaging.core.NotificationMessagingTemplate;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class SNSPublisher {

    private final NotificationMessagingTemplate template;
    private final Logger log = LoggerFactory.getLogger(SNSPublisher.class);

    public SNSPublisher(AmazonSNS tmp) {
        this.template = new NotificationMessagingTemplate(tmp);
    }

    @EventListener
    public void on(SignedOnWaitlistEvent evt) {
        try {
            template.sendNotification("newMembers", "Ny person på väntelistan! " + evt.getEmail(), "Ny person på väntelistan");
        }catch(Exception ex) {
            log.error("Could not send SNS-notification", ex);
        }
    }
}
