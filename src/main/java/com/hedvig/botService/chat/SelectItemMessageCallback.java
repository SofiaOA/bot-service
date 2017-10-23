package com.hedvig.botService.chat;

import com.hedvig.botService.enteties.SelectItem;
import com.hedvig.botService.enteties.UserContext;

public interface SelectItemMessageCallback {
    String operation(UserContext uc, SelectItem item);
}
