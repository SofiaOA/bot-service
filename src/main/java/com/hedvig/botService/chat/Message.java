package com.hedvig.botService.chat;

/*
 * Base class for interaction between Hedvig and members
 * */

public class Message {

	public String id;
	public MessageHeader header;
	public MessageBody body;
	
	public Message(String id, MessageHeader header, MessageBody body) {
		super();
		this.id = id;
		this.header = header;
		this.body = body;
	}
	public Message(){}

	public String toString(){
		return "[id:" + id + " header:" + header + " body("+body.getClass()+"):" + body + "]";
	}

}
