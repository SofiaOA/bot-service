package com.hedvig.botService.chat;

import com.hedvig.botService.enteties.UserContext;
import com.hedvig.botService.serviceIntegration.memberService.exceptions.ErrorType;

public interface BankIdChat {
    void bankIdAuthComplete(UserContext userContext);

    void bankIdAuthGeneralError(UserContext userContext);

    void memberSigned(String referenceId, UserContext userContext);

    void bankIdSignError(UserContext uc);

    void oustandingTransaction(UserContext uc);

    void noClient(UserContext uc);

    void started(UserContext uc);

    void userSign(UserContext uc);

    void expiredTransaction(UserContext uc);

    void certificateError(UserContext uc);
    void userCancel(UserContext uc);
    void cancelled(UserContext uc);
    void startFailed(UserContext uc);

    void couldNotLoadMemberProfile(UserContext uc);

    void signalSignFailure(ErrorType errorType, String detail, UserContext uc);

    void signalAuthFailiure(ErrorType errorType, String detail, UserContext uc);
}
