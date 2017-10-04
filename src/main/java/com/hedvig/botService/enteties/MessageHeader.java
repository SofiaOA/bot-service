package com.hedvig.botService.enteties;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class MessageHeader {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Integer messageId;

	public MessageHeader(long hedvigUserId, String responsePath, double timeStamp) {
		this.fromId = hedvigUserId;
		//this.type = type;
		this.responsePath = responsePath;
		this.timeStamp = timeStamp;
	}
	
	/*public MessageHeader(int fromId, String type, String responsePath, double timeStamp) {
		this(fromId, Type.valueOf(type), responsePath,timeStamp);
	}*/
	public MessageHeader() {
	}
	/*
	 * Header elements
	 * */
	public long fromId;
	public String responsePath;
	public double timeStamp; // Time when sent/recieved on API-GW

	
	/*@JsonSetter("type")
	public void setType(String t){
		this.type = Type.valueOf(t);
	}*/
}
