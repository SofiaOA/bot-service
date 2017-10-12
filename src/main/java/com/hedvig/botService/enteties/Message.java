package com.hedvig.botService.enteties;

/*
 * Base class for interaction between Hedvig and members
 * */

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.Instant;

@Entity
public class Message {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer globalId;

    public String id;

	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name="header_id")
	public MessageHeader header;

	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name="body2_id")
	public MessageBody body;

	@NotNull
	@Column(nullable = false)
    @Getter
    @Setter
	private Instant timestamp;

	public Integer getGlobalId() {
		return globalId;
	}

	@NotNull
	@ManyToOne
    @JsonIgnore
	public MemberChat chat;

	public Message(){}

	public String toString(){
		return "[globalId:" + globalId + " id:" + id + " header:" + header + " body("+body.getClass()+"):" + body + "]";
	}

}
