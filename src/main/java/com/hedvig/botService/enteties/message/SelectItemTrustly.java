package com.hedvig.botService.enteties.message;

import com.hedvig.botService.enteties.UserContext;

public class SelectItemTrustly extends SelectItem {

    static final long serialVersionUID = 1L;

    public String id;

    public SelectItemTrustly(String text, String value) {
        super(false, text, value);
    }

    @Override
    public void render(UserContext userContext) {
        super.render(userContext);

        this.id = userContext.replaceWithContext(UserContext.TRUSTLY_TRIGGER_ID);
    }


}
