package com.hedvig.botService.enteties;

import lombok.Data;
import org.assertj.core.util.diff.myers.Equalizer;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Data
public class DirectDebitMandateTrigger implements Serializable {

    @Id
    @GeneratedValue
    @Column( columnDefinition = "uuid", updatable = false )
    UUID id;

    String firstName;
    String lastName;
    String email;
    String ssn;

    String url;

    @NotNull
    String memberId;

    String orderId;
}
