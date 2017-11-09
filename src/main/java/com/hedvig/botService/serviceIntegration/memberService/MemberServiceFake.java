package com.hedvig.botService.serviceIntegration.memberService;

import com.hedvig.botService.enteties.userContextHelpers.UserData;
import com.hedvig.botService.serviceIntegration.memberService.dto.BankIdAuthResponse;
import com.hedvig.botService.serviceIntegration.memberService.dto.BankIdSignResponse;
import com.hedvig.botService.serviceIntegration.memberService.dto.BankIdStatusType;
import com.hedvig.botService.web.dto.Member;
import java.time.LocalDate;

import org.apache.commons.collections.Factory;
import org.springframework.http.HttpStatus;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.function.Supplier;

public class MemberServiceFake implements MemberService {
    @Override
    public Optional<BankIdAuthResponse> auth() {

        return Optional.of(new BankIdAuthResponse(BankIdStatusType.STARTED, "autostartToken", "referenceToken", null));
    }

    @Override
    public Optional<BankIdAuthResponse> auth(String ssn) {
        return Optional.empty();
    }

    @Override
    public String startBankAccountRetrieval(String memberId, String bankShortId) {
        return "";
    }

    @Override
    public Optional<BankIdSignResponse> sign(String ssn, String userMessage) {
        return Optional.empty();
    }

    @Override
    public void finalizeOnBoarding(String memberId, UserData data) {
        throw new NotImplementedException();
    }

    @Retryable(RestClientException.class)
    @Override
    public BankIdAuthResponse collect(String referenceToken, String memberId) {

        Supplier<BankIdStatusType> factory = () -> {
            try {
                return BankIdStatusType.valueOf(referenceToken);
            } catch (IllegalArgumentException ex) {
                return BankIdStatusType.ERROR;
            }
        };

//        throw new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR);

        return new BankIdAuthResponse(factory.get(), "autostartToken", referenceToken, memberId);
    }

    @Override
    public Member getProfile(String hid) {
        return new Member(1337l, "121212121212", "sven", "svensson", "stt", "cty", "123", "ema@sadf.com", "9994004", "SE", LocalDate.now());
    }
}
