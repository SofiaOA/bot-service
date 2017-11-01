package com.hedvig.botService.serviceIntegration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Component
public class MemberService {

    Logger log = LoggerFactory.getLogger(MemberService.class);

    private RestTemplate template;

    @Value("${hedvig.member-service.location:localhost:4084}")
    private String memberServiceLocation;

    @Autowired
    MemberService(RestTemplate restTemplate) {

        this.template = restTemplate;
    }

    public Optional<BankIdAuthResponse> auth() {
        return auth(null);
    }

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

    public String  startBankAccountRetrieval(String memberId, String bankShortId) {
        String url = "http://" + memberServiceLocation + "/i/member/" + memberId + "/startBankAccountRetrieval/" + bankShortId;
        HttpHeaders headers = new HttpHeaders();
        headers.add("hedvig.token", memberId);
        HttpEntity<String> h = new HttpEntity<>("", headers);
        ResponseEntity<Created> response = template.postForEntity(url, h, Created.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException(String.format("Could not start bankaccount retrieval. Respons from memberservice: %s", response.getStatusCode()));
        }

        return response.getBody().id;

    }


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
}
