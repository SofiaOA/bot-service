package com.hedvig.botService.commands;

import java.time.LocalDate;

import org.axonframework.commandhandling.TargetAggregateIdentifier;

import lombok.Value;

@Value
public class CreateUserCommand {

	@TargetAggregateIdentifier
    public String id;
	public String userId;
    private String name;
    private LocalDate birthDate;

    public CreateUserCommand(String userId, String id, String name, LocalDate birthDate) {
        System.out.println("CreateUserCommand");
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.birthDate = birthDate;
        System.out.println(this.toString());
    }
}
