package com.hedvig.botService.enteties;

public class SelectLink extends SelectItem {

	public SelectLink(String text, String uRI, String param, boolean selected) {
		this.text = text;
		this.URI = uRI;
		this.selected = selected;
		this.param = param;
	}
	public SelectLink(){}  // NOTE! All objects need to have a default constructor in order for Jackson to marshall.

	public String type = "link";
	public String text;
	public Boolean selected;
	public String URI;
	public String param;
}