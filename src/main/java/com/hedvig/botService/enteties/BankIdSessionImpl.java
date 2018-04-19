package com.hedvig.botService.enteties;

import com.hedvig.botService.serviceIntegration.memberService.dto.BankIdProgressStatus;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Data
@ToString(exclude = "userContext")
public class BankIdSessionImpl implements BankIdSession {

    private Boolean done;

    @Override
    public boolean shouldAbort() {
        if(errorCount == null) {
            errorCount = 0;
        }
        return errorCount > 3;
    }

    @Override
    public void setDone() {
        this.done = true;
    }

    @Override
    public boolean isDone() {
        return this.done == null ? false : this.done;
    }

    ;

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


    public BankIdSessionImpl() {
    }

    @Override
    public void addError() {
        if(errorCount == null) {
            errorCount = 0;
        }
        errorCount++;
    }

    @Override
    public void update(BankIdProgressStatus bankIdStatus) {
        this.lastStatus = bankIdStatus.name();
    }

}
