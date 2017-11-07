package com.hedvig.botService.enteties.message;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("bankidCollect")
public class MessageBodyBankIdCollect extends MessageBody {
    public String referenceId;

    public MessageBodyBankIdCollect(){}

    public MessageBodyBankIdCollect(String referenceId) {
        super("");
        this.referenceId = referenceId;
    }
}
