package com.hedvig.botService.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import net.logstash.logback.encoder.org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

@Component
public class InternalJwtToken {

    private String token;

    public InternalJwtToken(@Value("${oauth.secret:}") String jwtSecret) throws UnsupportedEncodingException {
        Algorithm algorithm = StringUtils.isBlank(jwtSecret) ? Algorithm.none() : Algorithm.HMAC256(jwtSecret);
        token = JWT.create().withIssuer("hedvig-internal").sign(algorithm);
    }

    public String getInternalToken() {
        return token;
    }

}
