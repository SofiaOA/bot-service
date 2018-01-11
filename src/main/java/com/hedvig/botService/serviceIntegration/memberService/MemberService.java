package com.hedvig.botService.serviceIntegration.memberService;

import com.hedvig.botService.enteties.userContextHelpers.UserData;
import com.hedvig.botService.serviceIntegration.memberService.dto.BankIdAuthResponse;
import com.hedvig.botService.serviceIntegration.memberService.dto.BankIdCollectResponse;
import com.hedvig.botService.serviceIntegration.memberService.dto.BankIdSignResponse;
import com.hedvig.botService.web.dto.Member;

import java.util.Optional;

public interface MemberService {
    Optional<BankIdAuthResponse> auth(String memberId);

    Optional<BankIdAuthResponse> auth(String ssn, String memberId);

    String  startBankAccountRetrieval(String memberId, String bankShortId);

    Optional<BankIdSignResponse> sign(String ssn, String userMessage, String memberId);

    void finalizeOnBoarding(String memberId, UserData data);

    BankIdCollectResponse collect(String referenceToken, String memberId);

    Member convertToFakeUser(String memberId);

    Member getProfile(String hid);

    void startOnBoardingWithSSN(String memberId, String ssn);
}
