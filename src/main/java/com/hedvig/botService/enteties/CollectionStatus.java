package com.hedvig.botService.enteties;

import com.hedvig.botService.serviceIntegration.memberService.dto.BankIdStatusType;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.time.Duration;
import java.time.Instant;

@Entity
@Data
public class CollectionStatus {

    private static Logger log = LoggerFactory.getLogger(CollectionStatus.class);

    public boolean shouldAbort() {
        if(errorCount == null) {
            errorCount = 0;
        }
        return errorCount > 3;
    }


    public enum CollectionType { AUTH, SIGN };

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String referenceToken;

    private String autoStartToken;

    private CollectionType collectionType;

    @ManyToOne
    private UserContext userContext;

    private String lastStatus;

    @Version
    private Long version;

    private Instant lastCallTime;

    private Integer errorCount = 0;


    public CollectionStatus() {
    }

    public void addError() {
        if(errorCount == null) {
            errorCount = 0;
        }
        errorCount++;
    }

    public void update(BankIdStatusType bankIdStatus) {
        this.lastStatus = bankIdStatus.name();
    }

    public boolean done() {
        return lastStatus.equals("COMPLETE") || lastStatus.equals("ERROR");
    }

    public boolean allowedToCall() {
        Instant now = Instant.now();
        log.debug("Last call time: {}, currentTime: {}", lastCallTime, now);
        return Duration.between(lastCallTime, now).toMillis() > 1000;
    }
}
