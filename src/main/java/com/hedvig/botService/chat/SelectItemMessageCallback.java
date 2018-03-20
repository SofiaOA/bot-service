package com.hedvig.botService.chat;

import com.hedvig.botService.enteties.UserContext;
import com.hedvig.botService.enteties.message.MessageBodySingleSelect;

public interface SelectItemMessageCallback {
    String operation(MessageBodySingleSelect message, UserContext uc);
}
