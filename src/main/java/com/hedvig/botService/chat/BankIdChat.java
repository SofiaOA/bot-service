package com.hedvig.botService.chat;

import com.hedvig.botService.enteties.MemberChat;
import com.hedvig.botService.enteties.UserContext;

public interface BankIdChat {
    void bankIdAuthComplete(UserContext userContext);

    void bankIdAuthError(UserContext userContext);

    void memberSigned(String referenceId, UserContext userContext);

    void bankIdSignError(UserContext uc);
}
