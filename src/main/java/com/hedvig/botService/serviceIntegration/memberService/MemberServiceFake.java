package com.hedvig.botService.serviceIntegration.memberService;

import com.hedvig.botService.enteties.userContextHelpers.UserData;
import com.hedvig.botService.serviceIntegration.memberService.dto.*;
import com.hedvig.botService.web.dto.Member;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.client.RestClientException;

import java.time.LocalDate;
import java.util.Optional;
import java.util.function.Supplier;

public class MemberServiceFake implements MemberService {
    @Override
    public Optional<BankIdAuthResponse> auth(String memberId) {

        return Optional.of(new BankIdAuthResponse(BankIdStatusType.STARTED, "autostartToken", "referenceToken"));
    }

    @Override
    public Optional<BankIdAuthResponse> auth(String ssn, String memberId) {
        return Optional.empty();
    }

    @Override
    public String startBankAccountRetrieval(String memberId, String bankShortId) {
        return "";
    }

    @Override
    public Optional<BankIdSignResponse> sign(String ssn, String userMessage, String memberId) {
        return Optional.empty();
    }

    @Override
    public void finalizeOnBoarding(String memberId, UserData data) {
        return;
    }

    @Retryable(RestClientException.class)
    @Override
    public BankIdCollectResponse collect(String referenceToken, String memberId) {

        Supplier<BankIdProgressStatus> factory = () -> {
            try {
                return BankIdProgressStatus.valueOf(referenceToken);
            } catch (IllegalArgumentException ex) {
                return BankIdProgressStatus.OUTSTANDING_TRANSACTION;
            }
        };

//        throw new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR);

        return new BankIdCollectResponse(factory.get(),  referenceToken, memberId);
    }

    @Override
    public Member convertToFakeUser(String memberId) {
        return new Member(Long.parseLong(memberId), "fakessn", "Mr test", "Skenson", "Gatan", "Storstan", "11123", "email@a.com", "070123244", "SE", LocalDate.parse("1980-01-01"));
    }

    @Override
    public Member getProfile(String hid) {
        return new Member(1337l, "121212121212", "sven", "svensson", "stt", "cty", "123", "ema@sadf.com", "9994004", "SE", LocalDate.now());
    }

    @Override
    public void startOnBoardingWithSSN(String memberId, String ssn) {
        return;
    }
}
