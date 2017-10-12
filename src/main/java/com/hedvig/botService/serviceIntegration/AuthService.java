package com.hedvig.botService.serviceIntegration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class AuthService {

    RestTemplate template;

    @Autowired
    AuthService(RestTemplate restTemplate) {
        this.template = restTemplate;
    }

    public BankIdAuthResponse auth() {
        ResponseEntity<BankIdAuthResponse> response = template.postForEntity("http://member-service/member/bankid/auth", "", BankIdAuthResponse.class);

        return response.getBody();
    }

}
