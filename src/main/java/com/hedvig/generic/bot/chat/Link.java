package com.hedvig.generic.bot.chat;

public class Link {

	public Link(String text, String uRI, String param, boolean selected) {
		this.text = text;
		this.URI = uRI;
		this.selected = selected;
		this.param = param;
	}
	public Link(){}  // NOTE! All objects need to have a default constructor in order for Jackson to marshall.
	public String text;
	public boolean selected;
	public String URI;
	public String param;
	
}