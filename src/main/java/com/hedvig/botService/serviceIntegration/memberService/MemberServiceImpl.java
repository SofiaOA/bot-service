package com.hedvig.botService.serviceIntegration.memberService;

import com.hedvig.botService.enteties.userContextHelpers.UserData;
import com.hedvig.botService.serviceIntegration.memberService.dto.*;
import com.hedvig.botService.web.dto.Member;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.net.URI;
import java.util.HashMap;
import java.util.Optional;

@Component
public class MemberServiceImpl implements MemberService {

    Logger log = LoggerFactory.getLogger(MemberServiceImpl.class);

    private RestTemplate template;

    @Value("${hedvig.member-service.location:localhost:4084}")
    private String memberServiceLocation;

    @Autowired
    MemberServiceImpl(RestTemplate restTemplate) {

        this.template = restTemplate;
    }

    @Override
    public Optional<BankIdAuthResponse> auth() {
        return auth(null);
    }

    @Override
    public Optional<BankIdAuthResponse> auth(String ssn) {
         String url = "http://" + memberServiceLocation + "/member/bankid/auth";
        try {
            BankIdAuthRequest req = new BankIdAuthRequest(ssn);

            ResponseEntity<BankIdAuthResponse> response = template.postForEntity(url, req, BankIdAuthResponse.class);
            if(response.getStatusCode() == HttpStatus.OK) {
                return Optional.ofNullable(response.getBody());
            }else {
                log.error("Could not make request to {}: {}", url, response.toString());
            }
        } catch (Exception e) {
            log.error("Could not authenticate: ", e);
        }

        return Optional.empty();
    }

    @Override
    public String  startBankAccountRetrieval(String memberId, String bankShortId) {
        String url = "http://" + memberServiceLocation + "/i/member/" + memberId + "/startBankAccountRetrieval/" + bankShortId;
        HttpEntity<String> h = createHeaders(memberId);
        ResponseEntity<Created> response = template.postForEntity(url, h, Created.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException(String.format("Could not start bankaccount retrieval. Respons from memberservice: %s", response.getStatusCode()));
        }

        return response.getBody().id;

    }

    private HttpEntity<String> createHeaders(String memberId) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("hedvig.token", memberId);
        return new HttpEntity<>("", headers);
    }


    @Override
    public Optional<BankIdSignResponse> sign(String ssn, String userMessage) {

        String url = "http://" + memberServiceLocation + "/member/bankid/sign";
        try {
            BankIdSignRequest req = new BankIdSignRequest(ssn, userMessage);

            ResponseEntity<BankIdSignResponse> response = template.postForEntity(url, req, BankIdSignResponse.class);
            if(response.getStatusCode().is2xxSuccessful()) {
                return Optional.of(response.getBody());
            }else {
                log.error("Could not make request to {}: {}", url, response.toString());
            }
        } catch (Exception e) {
            log.error("Could not start sign request: ", e);
        }

        return Optional.empty();
    }

    @Override
    public void finalizeOnBoarding(String memberId, UserData data) {
        String url = "http://" + memberServiceLocation + "/i/member/finalizeOnboarding";
        try {
            FinalizeOnBoardingRequest req = new FinalizeOnBoardingRequest();
            req.setFirstName(data.getFirstName());
            req.setLastName(data.getFamilyName());
            req.setMemberId(memberId);
            req.setSsn(data.getSSN());
            req.setEmail(data.getEmail());

            Address address = new Address();
            address.setStreet(data.getAddressStreet());
            address.setCity(data.getAddressCity());
            address.setZipCode(data.getAddressZipCode());
            req.setAddress(address);

            ResponseEntity<FinalizeOnBoardingResponse> response = template.postForEntity(url, req, FinalizeOnBoardingResponse.class);

        }catch (RestClientResponseException ex) {
            log.error("Could not finalize member {}", memberId, ex);
        }
    }

    @Retryable
    @Override
    public BankIdAuthResponse collect(String referenceToken, String memberId) {
        UriTemplate url = new UriTemplate("http://" + memberServiceLocation + "/member/bankid/collect?referenceToken={referenceToken}&memberId={memberId}");

        URI expandedUri = url.expand(new HashMap<String, String>(){{
            put("referenceToken", referenceToken);
            put("memberId", memberId);
        }});

        ResponseEntity<BankIdAuthResponse> response = template.postForEntity(expandedUri, createHeaders(memberId), BankIdAuthResponse.class);

        return response.getBody();
    }

    @Override
    public Member convertToFakeUser(String memberId) {
        throw new RuntimeException("Cannot create fake user in live environment!");
    }

    @Override
    public Member getProfile(String memberId) {
        UriTemplate url = new UriTemplate("http://" + memberServiceLocation + "/member/{memberId}");

        URI expandedUri = url.expand(new HashMap<String, String>(){{
            put("memberId", memberId);
        }});

        ResponseEntity<Member> response = template.getForEntity(expandedUri, Member.class);
        return response.getBody();
    }
}
