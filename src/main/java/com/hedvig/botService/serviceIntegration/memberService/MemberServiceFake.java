package com.hedvig.botService.serviceIntegration.memberService;

import com.hedvig.botService.enteties.userContextHelpers.UserData;
import com.hedvig.botService.serviceIntegration.memberService.dto.*;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.client.RestClientException;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
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
    public MemberProfile convertToFakeUser(String memberId) {
        MemberAddress address = new MemberAddress("Gatan", "Storstan", "11123", "1001", 0);
        return new MemberProfile(memberId, "fakessn", "Mr test", "Skenson", Optional.of(address), "email@a.com", "070123244", "SE", LocalDate.parse("1980-01-01"));
    }

    @Override
    public MemberProfile getProfile(String hid) {
        return new MemberProfile("1337", "121212121212", "sven", "svensson", Optional.empty(),"ema@sadf.com", "9994004", "SE", LocalDate.now());
    }

    @Override
    public void startOnBoardingWithSSN(String memberId, String ssn) {
        return;
    }

    @Override
    public void sendSignupMail(String email, UUID uuid) {

    }

    @Override
    public void sendOnboardedActiveLater(String email, String name, String memberId) {
        return;
    }

    @Override
    public void sendOnboardedActiveToday(String email, String name) {
        return;
    }

    @Override
    public void selectCashback(String memberId, UUID charityId) {

    }
}