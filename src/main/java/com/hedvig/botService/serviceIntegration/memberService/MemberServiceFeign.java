package com.hedvig.botService.serviceIntegration.memberService;

import com.hedvig.botService.enteties.userContextHelpers.UserData;
import com.hedvig.botService.serviceIntegration.memberService.dto.Address;
import com.hedvig.botService.serviceIntegration.memberService.dto.BankIdAuthRequest;
import com.hedvig.botService.serviceIntegration.memberService.dto.BankIdAuthResponse;
import com.hedvig.botService.serviceIntegration.memberService.dto.BankIdCollectResponse;
import com.hedvig.botService.serviceIntegration.memberService.dto.BankIdSignRequest;
import com.hedvig.botService.serviceIntegration.memberService.dto.BankIdSignResponse;
import com.hedvig.botService.serviceIntegration.memberService.dto.FinalizeOnBoardingRequest;
import com.hedvig.botService.serviceIntegration.memberService.dto.SendOnboardedActiveTodayRequest;
import com.hedvig.botService.serviceIntegration.memberService.dto.SendSignupRequest;
import com.hedvig.botService.serviceIntegration.memberService.dto.StartOnboardingWithSSNRequest;
import com.hedvig.botService.serviceIntegration.memberService.dto.UpdateEmailRequest;
import com.hedvig.botService.web.dto.Member;
import feign.FeignException;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;

@Service
public class MemberServiceFeign implements MemberService {

  private final Logger log = LoggerFactory.getLogger(MemberServiceFeign.class);
  private final MemberServiceClient client;

  public MemberServiceFeign(MemberServiceClient client) {

    this.client = client;
  }

  @Override
  public Optional<BankIdAuthResponse> auth(String memberId) {
    return auth(null, memberId);
  }

  @Override
  public Optional<BankIdAuthResponse> auth(String ssn, String memberId) {
    BankIdAuthRequest authRequest = new BankIdAuthRequest(null, memberId);
    try {
      ResponseEntity<BankIdAuthResponse> auth = this.client.auth(authRequest);
      return Optional.of(auth.getBody());
    } catch (Throwable ex) {
      log.error("Got error response when calling memberService.auth", ex);
      return Optional.empty();
    }
  }

  @Override
  public Optional<BankIdSignResponse> sign(String ssn, String userMessage, String memberId) {
    BankIdSignRequest request = new BankIdSignRequest(ssn, userMessage, memberId);
    try {
      ResponseEntity<BankIdSignResponse> response = this.client.sign(request);
      return Optional.of(response.getBody());
    } catch (Throwable ex) {
      log.error("Got error response when calling memberService.sign", ex);
      return Optional.empty();
    }
  }

  @Override
  public BankIdSignResponse signEx(String ssn, String userMessage, String memberId) {
    BankIdSignRequest request = new BankIdSignRequest(ssn, userMessage, memberId);
    ResponseEntity<BankIdSignResponse> response = this.client.sign(request);
    return response.getBody();
  }

  @Override
  public void finalizeOnBoarding(String memberId, UserData data) {

    FinalizeOnBoardingRequest req = new FinalizeOnBoardingRequest();
    req.setFirstName(data.getFirstName());
    req.setLastName(data.getFamilyName());
    req.setMemberId(memberId);
    req.setSsn(data.getSSN());
    req.setEmail(data.getEmail());
    req.setPhoneNumber(data.getPhoneNumber());

    Address address = new Address();
    address.setStreet(data.getAddressStreet());
    address.setCity(data.getAddressCity());
    address.setZipCode(data.getAddressZipCode());
    address.setFloor(data.getFloor());
    req.setAddress(address);
    try {
      this.client.finalizeOnBoarding(memberId, req);
    } catch (RestClientResponseException ex) {
      log.error("Could not finalize member {}", memberId, ex);
    }
  }

  @Override
  public BankIdCollectResponse collect(String referenceToken, String memberId) {

    ResponseEntity<BankIdCollectResponse> collect = this.client.collect(memberId, referenceToken);
    return collect.getBody();
  }

  @Override
  public MemberProfile convertToFakeUser(String memberId) {
    throw new RuntimeException("Cannot create fake user in live environment!");
  }

  @Override
  public MemberProfile getProfile(String memberId) {

    final Member profile = this.client.profile(memberId).getBody();
    MemberAddress address = null;
    if (profile.getStreet() != null && profile.getZipCode() != null && profile.getCity() != null) {
      address =
          new MemberAddress(
              profile.getStreet(),
              profile.getCity(),
              profile.getZipCode(),
              profile.getApartment(),
              profile.getFloor() == null ? 0 : profile.getFloor());
    }

    return new MemberProfile(
        memberId,
        profile.getSsn(),
        profile.getFirstName(),
        profile.getLastName(),
        Optional.ofNullable(address),
        "",
        profile.getPhoneNumber(),
        profile.getCountry(),
        profile.getBirthDate());
  }

  @Override
  public void startOnBoardingWithSSN(String memberId, String ssn) {
    this.client.startOnBoardingWithSSN(memberId, new StartOnboardingWithSSNRequest(ssn));
  }

  @Override
  public void sendSignupMail(String email, UUID uuid) {
    final Long requestId = Math.round(Math.random() * 1000);
    final SendSignupRequest sendSignupRequest =
        new SendSignupRequest(uuid, email, requestId.toString());
    this.client.sendSignup(sendSignupRequest);
  }

  @Override
  public void sendOnboardedActiveToday(String email, String name) {
    send(
        () ->
            this.client.sendOnboardedActiveToday(new SendOnboardedActiveTodayRequest(name, email)));
  }

  @Override
  public void selectCashback(String memberId, UUID charityId) {
    send(() -> this.client.selectCashback(memberId, charityId));
  }

  @Override
  public void updateEmail(String memberId, String email) {
    send(() -> this.client.updateEmail(memberId, new UpdateEmailRequest(email)));
  }

  private void send(Runnable supplier) {
    try {
      supplier.run();
    } catch (FeignException ex) {
      log.error("Could not send request to member-service", ex);
    }
  }
}
