package com.hedvig.botService.chat;

import com.hedvig.botService.enteties.UserContext;
import com.hedvig.botService.enteties.message.MessageBodySingleSelect;
import com.hedvig.botService.enteties.message.SelectItem;

public interface SelectItemMessageCallback {
    String operation(MessageBodySingleSelect message, UserContext uc);
}
