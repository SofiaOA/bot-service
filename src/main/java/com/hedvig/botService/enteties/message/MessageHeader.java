package com.hedvig.botService.enteties.message;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

@Entity
public class MessageHeader {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Integer messageId;

	public MessageHeader(long hedvigUserId, String responsePath, long timeStamp) {
		this.fromId = hedvigUserId;
		//this.type = type;
		this.responsePath = responsePath;
		this.timeStamp = timeStamp;
		this.pollingInterval = 1000l; // Default value = 1s
		this.loadingIndicator = "loader"; // Default value
		this.shouldRequestPushNotifications = false;
	}
	
	
	/*public MessageHeader(int fromId, String type, String responsePath, double timeStamp) {
		this(fromId, Type.valueOf(type), responsePath,timeStamp);
	}*/
	public MessageHeader() {
	}
	/*
	 * Header elements
	 * */
	public Long fromId;
	public String responsePath;
	public Long timeStamp; // Time when sent/recieved on API-GW
	public String loadingIndicator; // Link to animation to show during load
	public String avatarName; // Link to avatar animation to show over message
	public Long pollingInterval; // Frequency of next request
	
	@Transient
	public boolean editAllowed; // For client use to indicate if the last message is editable
	/*@JsonSetter("type")
	public void setType(String t){
		this.type = Type.valueOf(t);
	}*/
	public boolean shouldRequestPushNotifications; // Should responding to this message prompt user to turn on push notifications
}
