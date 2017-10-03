package com.hedvig.botService.aggregates;

import static org.axonframework.commandhandling.model.AggregateLifecycle.apply;

import java.time.LocalDate;

import com.hedvig.botService.events.UserCreatedEvent;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.spring.stereotype.Aggregate;

import com.hedvig.botService.commands.CreateUserCommand;

/**
 * This is an example Aggregate and should be remodeled to suit the needs of you domain.
 */
@Aggregate
public class UserAggregate {

    @AggregateIdentifier
    public String id;

    public String name;

    public LocalDate birthDate;

    public UserAggregate(){
        System.out.println("Hejsan");
    }

    @CommandHandler
    public UserAggregate(CreateUserCommand command) {
        System.out.println("create");
        apply(new UserCreatedEvent(command.getId(), command.getName(), command.getBirthDate()));
    }

    @EventSourcingHandler
    public void on(UserCreatedEvent e) {
        this.id = e.getId();
        this.name = e.getName();
        this.birthDate = e.getBirthDate();
    }
}
