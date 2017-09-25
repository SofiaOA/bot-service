package com.hedvig.generic.bot.chat;

public class MessageHeader {

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
