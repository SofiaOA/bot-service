package com.hedvig.botService.enteties.message;

import com.hedvig.botService.enteties.UserContext;

public class SelectLink extends SelectItem {

	static final long serialVersionUID = 1L;

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

	@Override
	public void render(final UserContext context) {
		if(this.appUrl != null) {
			this.appUrl = context.replaceWithContext(this.appUrl);
		}
		if(this.webUrl != null) {
			this.webUrl = context.replaceWithContext(this.webUrl);
		}
	}
}