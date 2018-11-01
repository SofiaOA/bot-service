package com.hedvig.botService.serviceIntegration.memberService;

import com.hedvig.botService.serviceIntegration.memberService.dto.BankIdAuthRequest;
import com.hedvig.botService.serviceIntegration.memberService.dto.BankIdAuthResponse;
import com.hedvig.botService.serviceIntegration.memberService.dto.BankIdCollectResponse;
import com.hedvig.botService.serviceIntegration.memberService.dto.BankIdSignRequest;
import com.hedvig.botService.serviceIntegration.memberService.dto.BankIdSignResponse;
import com.hedvig.botService.serviceIntegration.memberService.dto.FinalizeOnBoardingRequest;
import com.hedvig.botService.serviceIntegration.memberService.dto.FinalizeOnBoardingResponse;
import com.hedvig.botService.serviceIntegration.memberService.dto.StartOnboardingWithSSNRequest;
import com.hedvig.botService.serviceIntegration.memberService.dto.UpdateEmailRequest;
import com.hedvig.botService.web.dto.Member;
import java.util.UUID;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    name = "memberServiceClient",
    url = "${hedvig.member-service.url:member-service}",
    configuration = FeignConfiguration.class)
public interface MemberServiceClient {

  @RequestMapping(value = "/member/bankid/auth", method = RequestMethod.POST)
  ResponseEntity<BankIdAuthResponse> auth(@RequestBody BankIdAuthRequest request);

  @RequestMapping(value = "/member/bankid/sign", method = RequestMethod.POST)
  ResponseEntity<BankIdSignResponse> sign(@RequestBody BankIdSignRequest request);

  @RequestMapping(value = "/member/bankid/collect", method = RequestMethod.POST)
  ResponseEntity<BankIdCollectResponse> collect(
      @RequestHeader("hedvig.token") String memberId,
      @RequestParam("referenceToken") String referenceToken);

  @RequestMapping(value = "/i/member/{memberId}", method = RequestMethod.GET)
  ResponseEntity<Member> profile(@PathVariable("memberId") String memberId);

  @RequestMapping(value = "/i/member/{memberId}/startOnboardingWithSSN")
  ResponseEntity<Void> startOnBoardingWithSSN(
      @PathVariable("memberId") String memberId,
      @RequestBody StartOnboardingWithSSNRequest request);

  @RequestMapping(value = "/i/member/{memberId}/finalizeOnboarding")
  ResponseEntity<FinalizeOnBoardingResponse> finalizeOnBoarding(
      @PathVariable("memberId") String memberId, @RequestBody FinalizeOnBoardingRequest req);

  @RequestMapping(value = "/i/member/{memberId}/updateEmail")
  ResponseEntity<String> updateEmail(
      @PathVariable("memberId") String memberId, @RequestBody UpdateEmailRequest request);

  @RequestMapping(value = "/cashback", method = RequestMethod.POST)
  ResponseEntity<String> selectCashback(
      @RequestHeader("hedvig.token") String hid, @RequestParam("optionId") UUID optionId);
}
