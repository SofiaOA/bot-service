package com.hedvig.botService.enteties;

public class SelectLink extends SelectItem {

	public SelectLink(String text, String value, String view, String appUrl, String webUrl, boolean selected) {
		super(selected, text, value);
		this.view = view;
		this.appUrl = appUrl;
		this.webUrl = webUrl;
	}
	public SelectLink(){
    }  // NOTE! All objects need to have a default constructor in order for Jackson to marshall.

	public String view;
	public String appUrl;
	public String webUrl;
}