package com.hedvig.botService.serviceIntegration.memberService;

import org.springframework.web.client.HttpClientErrorException;

public interface BankIdChatStrategy {

    void onError();
    void onComplete();
    void onNoClient();
    void onOutstandingTransaction();
    void onStarted();
    void onUserReq();
    void onUserSign();
    void onException(HttpClientErrorException ex);
}
