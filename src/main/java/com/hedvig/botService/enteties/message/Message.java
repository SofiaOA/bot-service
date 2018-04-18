package com.hedvig.botService.enteties.message;

/*
 * Base class for interaction between Hedvig and members
 * */

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hedvig.botService.dataTypes.HedvigDataType;
import com.hedvig.botService.enteties.MemberChat;

import com.hedvig.botService.enteties.UserContext;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.Instant;

@Entity
@ToString
public class Message {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer globalId;

    public String id;

    @JsonIgnore
    public Boolean deleted; // We do not remove anything but mark deleted
    
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

	@Transient
	@JsonIgnore
	public HedvigDataType expectedType;
	
	public Message(){
		header = new MessageHeader();
	}

	public String toString(){
		return "[globalId:" + globalId + " id:" + id + " header:" + header + " body("+body.getClass()+"):" + body + "]";
	}


	public void render(UserContext userContext) {
		this.body.render(userContext);
	}

}
