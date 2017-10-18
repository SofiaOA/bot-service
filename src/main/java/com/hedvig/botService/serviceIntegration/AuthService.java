package com.hedvig.botService.serviceIntegration;

import org.apache.zookeeper.Op;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Component
public class AuthService {

    Logger log = LoggerFactory.getLogger(AuthService.class);

    private RestTemplate template;

    @Value("${hedvig.member-service.location:localhost:4084}")
    private String memberServiceLocation;

    @Autowired
    AuthService(RestTemplate restTemplate) {

        this.template = restTemplate;
    }

    public Optional<BankIdAuthResponse> auth() {
        String url = "http://" + memberServiceLocation + "/member/bankid/auth";
        try {


            ResponseEntity<BankIdAuthResponse> response = template.postForEntity(url, "", BankIdAuthResponse.class);
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

}
