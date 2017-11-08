package com.hedvig.botService.enteties;

import com.hedvig.botService.serviceIntegration.memberService.dto.BankIdStatusType;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class CollectionStatus {

    public CollectionStatus() {
    }

    public void update(BankIdStatusType bankIdStatus) {
        this.lastStatus = bankIdStatus.name();
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


    public boolean done() {
        return lastStatus.equals("COMPLETE") || lastStatus.equals("ERROR");
    }
}
