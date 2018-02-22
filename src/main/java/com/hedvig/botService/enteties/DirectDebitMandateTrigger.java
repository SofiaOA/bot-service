package com.hedvig.botService.enteties;

import com.hedvig.botService.serviceIntegration.paymentService.dto.OrderState;
import lombok.Data;
import org.assertj.core.util.diff.myers.Equalizer;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.UUID;

import static jdk.net.SocketFlow.Status.IN_PROGRESS;

@Entity
@Data
public class DirectDebitMandateTrigger implements Serializable {

    public enum TriggerStatus {
        CRATED,
        IN_PROGRESS,
        FAILED,
        SUCCESS
    }

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

    TriggerStatus  status;
}
